package org.coffeezip.auth

import io.smallrye.jwt.auth.principal.JWTParser
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.persistence.NoResultException
import jakarta.transaction.Transactional
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.coffeezip.entity.Member

data class OAuthCallbackRequest(val code: String)
data class RefreshRequest(val refreshToken: String)
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val memberId: Long,
    val nickname: String
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
    fun googleCallback(request: OAuthCallbackRequest): TokenResponse {
        val userInfo = oAuthService.exchangeGoogleCode(request.code)
        return processOAuthLogin(userInfo)
    }

    @POST
    @Path("/kakao/callback")
    @Transactional
    fun kakaoCallback(request: OAuthCallbackRequest): TokenResponse {
        val userInfo = oAuthService.exchangeKakaoCode(request.code)
        return processOAuthLogin(userInfo)
    }

    @POST
    @Path("/refresh")
    fun refresh(request: RefreshRequest): TokenResponse {
        val jwt = try {
            jwtParser.parse(request.refreshToken)
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

        val accessToken = jwtService.generateAccessToken(member.id!!, member.nickname)
        val refreshToken = jwtService.generateRefreshToken(member.id!!)

        return TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            memberId = member.id!!,
            nickname = member.nickname
        )
    }

    private fun processOAuthLogin(userInfo: OAuthUserInfo): TokenResponse {
        val member = findOrCreateMember(userInfo)
        val accessToken = jwtService.generateAccessToken(member.id!!, member.nickname)
        val refreshToken = jwtService.generateRefreshToken(member.id!!)
        return TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            memberId = member.id!!,
            nickname = member.nickname
        )
    }

    private fun findOrCreateMember(userInfo: OAuthUserInfo): Member {
        return try {
            entityManager.createQuery(
                "SELECT m FROM Member m WHERE m.provider = :provider AND m.providerId = :providerId",
                Member::class.java
            )
                .setParameter("provider", userInfo.provider)
                .setParameter("providerId", userInfo.providerId)
                .singleResult
        } catch (e: NoResultException) {
            val newMember = Member().apply {
                provider = userInfo.provider
                providerId = userInfo.providerId
                email = userInfo.email
                nickname = userInfo.nickname
                profileImage = userInfo.profileImage
            }
            entityManager.persist(newMember)
            newMember
        }
    }
}
