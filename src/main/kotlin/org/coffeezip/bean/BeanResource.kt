package org.coffeezip.bean

import io.quarkus.security.Authenticated
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.DefaultValue
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Response
import org.coffeezip.auth.AuthContext
import org.coffeezip.dto.CreateBeanRequest
import org.coffeezip.dto.UpdateBeanRequest
import org.coffeezip.dto.UpsertBeanReviewRequest

@Path("/api/beans")
@ApplicationScoped
class BeanResource {

    @Inject
    lateinit var beanService: BeanService

    @Inject
    lateinit var authContext: AuthContext

    @GET
    fun getBeans(
        @QueryParam("search") search: String?,
        @QueryParam("origin") origin: String?,
        @QueryParam("roastLevel") roastLevel: String?,
        @QueryParam("page") @DefaultValue("0") page: Int,
        @QueryParam("size") @DefaultValue("20") size: Int
    ) = beanService.getBeans(search, origin, roastLevel, page, size)

    @POST
    @Authenticated
    fun createBean(req: CreateBeanRequest): Response {
        val response = beanService.createBean(req, authContext.memberId)
        return Response.status(Response.Status.CREATED).entity(response).build()
    }

    @GET
    @Path("/{id}")
    fun getBeanDetail(@PathParam("id") id: Long) = beanService.getBeanDetail(id)

    @PUT
    @Path("/{id}")
    @Authenticated
    fun updateBean(
        @PathParam("id") id: Long,
        req: UpdateBeanRequest
    ) = beanService.updateBean(id, req, authContext.memberId)

    @GET
    @Path("/{id}/reviews")
    fun getReviews(
        @PathParam("id") id: Long,
        @QueryParam("page") @DefaultValue("0") page: Int,
        @QueryParam("size") @DefaultValue("20") size: Int
    ) = beanService.getReviews(id, page, size)

    @POST
    @Path("/{id}/reviews")
    @Authenticated
    fun upsertReview(
        @PathParam("id") id: Long,
        req: UpsertBeanReviewRequest
    ): Response {
        val response = beanService.upsertReview(id, req, authContext.memberId)
        return Response.status(Response.Status.CREATED).entity(response).build()
    }

    @DELETE
    @Path("/{id}/reviews/mine")
    @Authenticated
    fun deleteMyReview(@PathParam("id") id: Long): Response {
        beanService.deleteReview(id, authContext.memberId)
        return Response.noContent().build()
    }
}
