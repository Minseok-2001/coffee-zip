package org.coffeezip.member

import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.PATCH
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.coffeezip.auth.AuthContext
import org.coffeezip.entity.Member

data class MeResponse(
    val id: Long,
    val nickname: String,
    val email: String?,
    val profileImage: String?,
)

data class UpdateNicknameRequest(
    val nickname: String,
)

@Path("/me")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("**")
class MeResource {

    @Inject
    lateinit var authContext: AuthContext

    @Inject
    lateinit var entityManager: EntityManager

    @GET
    fun getMe(): MeResponse {
        val member = entityManager.find(Member::class.java, authContext.memberId)
            ?: throw WebApplicationException(Response.status(404).entity("Member not found").build())
        return MeResponse(
            id = member.id!!,
            nickname = member.nickname,
            email = member.email,
            profileImage = member.profileImage,
        )
    }

    @PATCH
    @Transactional
    fun updateNickname(req: UpdateNicknameRequest): MeResponse {
        val nickname = req.nickname.trim()
        if (nickname.isBlank() || nickname.length > 20) {
            throw WebApplicationException(
                Response.status(400).entity("닉네임은 1~20자 사이여야 합니다").build()
            )
        }
        val member = entityManager.find(Member::class.java, authContext.memberId)
            ?: throw WebApplicationException(Response.status(404).entity("Member not found").build())
        member.nickname = nickname
        return MeResponse(
            id = member.id!!,
            nickname = member.nickname,
            email = member.email,
            profileImage = member.profileImage,
        )
    }
}
