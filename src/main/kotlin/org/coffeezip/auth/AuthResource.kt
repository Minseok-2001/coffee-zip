package org.coffeezip.auth

import io.smallrye.jwt.auth.principal.JWTParser
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.persistence.NoResultException
import jakarta.transaction.Transactional
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.CookieParam
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.NewCookie
import jakarta.ws.rs.core.Response
import org.coffeezip.entity.Member

data class OAuthCallbackRequest(
    val code: String,
)

data class LoginResponse(
    val memberId: Long,
    val nickname: String,
)

@Path("/auth")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class AuthResource {
    @Inject
    lateinit var jwtService: JwtService

    @Inject
    lateinit var oAuthService: OAuthService

    @Inject
    lateinit var entityManager: EntityManager

    @Inject
    lateinit var jwtParser: JWTParser

    @POST
    @Path("/google/callback")
    @Transactional
    fun googleCallback(request: OAuthCallbackRequest): Response {
        val userInfo = oAuthService.exchangeGoogleCode(request.code)
        return buildAuthResponse(findOrCreateMember(userInfo))
    }

    @POST
    @Path("/refresh")
    fun refresh(@CookieParam("refreshToken") refreshTokenValue: String?): Response {
        if (refreshTokenValue == null) {
            throw WebApplicationException(Response.status(401).entity("No refresh token").build())
        }

        val jwt = try {
            jwtParser.parse(refreshTokenValue)
        } catch (e: Exception) {
            throw WebApplicationException(Response.status(401).entity("Invalid refresh token").build())
        }

        val tokenType = jwt.getClaim<String>("type")
        if (tokenType != "refresh") {
            throw WebApplicationException(Response.status(401).entity("Not a refresh token").build())
        }

        val memberId = jwt.subject?.toLongOrNull()
            ?: throw WebApplicationException(Response.status(401).entity("Invalid token subject").build())

        val member = entityManager.find(Member::class.java, memberId)
            ?: throw WebApplicationException(Response.status(404).entity("Member not found").build())

        return buildAuthResponse(member)
    }

    @POST
    @Path("/logout")
    fun logout(): Response {
        val clearAccess = NewCookie.Builder("accessToken")
            .value("").httpOnly(true).path("/").maxAge(0).build()
        val clearRefresh = NewCookie.Builder("refreshToken")
            .value("").httpOnly(true).path("/").maxAge(0).build()
        return Response.noContent().cookie(clearAccess, clearRefresh).build()
    }

    private fun buildAuthResponse(member: Member): Response {
        val accessToken = jwtService.generateAccessToken(member.id!!, member.nickname)
        val refreshToken = jwtService.generateRefreshToken(member.id!!)

        val accessCookie = NewCookie.Builder("accessToken")
            .value(accessToken)
            .httpOnly(true)
            .path("/")
            .maxAge(jwtService.accessTokenExpiry.toInt())
            .build()

        val refreshCookie = NewCookie.Builder("refreshToken")
            .value(refreshToken)
            .httpOnly(true)
            .path("/")
            .maxAge(jwtService.refreshTokenExpiry.toInt())
            .build()

        return Response.ok(LoginResponse(member.id!!, member.nickname))
            .cookie(accessCookie, refreshCookie)
            .build()
    }

    private fun findOrCreateMember(userInfo: OAuthUserInfo): Member =
        try {
            entityManager.createQuery(
                "SELECT m FROM Member m WHERE m.provider = :provider AND m.providerId = :providerId",
                Member::class.java,
            ).setParameter("provider", userInfo.provider)
                .setParameter("providerId", userInfo.providerId)
                .singleResult
        } catch (e: NoResultException) {
            val newMember = Member().apply {
                provider = userInfo.provider
                providerId = userInfo.providerId
                email = userInfo.email
                nickname = NicknameGenerator.generate()
                profileImage = userInfo.profileImage
            }
            entityManager.persist(newMember)
            newMember
        }
}
