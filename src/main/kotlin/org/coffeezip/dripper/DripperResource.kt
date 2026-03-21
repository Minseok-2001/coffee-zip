package org.coffeezip.dripper

import io.quarkus.security.Authenticated
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.coffeezip.auth.AuthContext
import org.coffeezip.dto.*

@Path("/drippers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
class DripperResource {

    @Inject
    lateinit var dripperService: DripperService

    @Inject
    lateinit var authContext: AuthContext

    @GET
    fun getDrippers(
        @QueryParam("search") search: String?,
        @QueryParam("type") type: String?,
        @QueryParam("material") material: String?,
        @QueryParam("page") @DefaultValue("0") page: Int,
        @QueryParam("size") @DefaultValue("20") size: Int
    ): List<DripperSummaryResponse> =
        dripperService.getDrippers(search, type, material, page, size)

    @POST
    @Authenticated
    fun createDripper(req: CreateDripperRequest): Response {
        val id = dripperService.createDripper(req, authContext.memberId)
        return Response.status(Response.Status.CREATED).entity(mapOf("id" to id)).build()
    }

    @GET
    @Path("/{id}")
    fun getDripperDetail(@PathParam("id") id: Long): DripperDetailResponse =
        dripperService.getDripperDetail(id)

    @PUT
    @Path("/{id}")
    @Authenticated
    fun updateDripper(@PathParam("id") id: Long, req: UpdateDripperRequest): DripperDetailResponse =
        dripperService.updateDripper(id, req, authContext.memberId)

    @GET
    @Path("/{id}/reviews")
    fun getReviews(
        @PathParam("id") id: Long,
        @QueryParam("page") @DefaultValue("0") page: Int,
        @QueryParam("size") @DefaultValue("20") size: Int
    ): List<DripperReviewResponse> =
        dripperService.getReviews(id, page, size)

    @POST
    @Path("/{id}/reviews")
    @Authenticated
    fun upsertReview(@PathParam("id") id: Long, req: UpsertDripperReviewRequest): Response {
        dripperService.upsertReview(id, req, authContext.memberId)
        return Response.status(Response.Status.CREATED).build()
    }

    @DELETE
    @Path("/{id}/reviews/mine")
    @Authenticated
    fun deleteMyReview(@PathParam("id") id: Long): Response {
        dripperService.deleteReview(id, authContext.memberId)
        return Response.noContent().build()
    }
}
