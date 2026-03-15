package org.coffeezip.recipe

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

@Path("/recipes")
@ApplicationScoped
class RecipeResource {

    @Inject
    lateinit var recipeService: RecipeService

    @Inject
    lateinit var authContext: AuthContext

    @GET
    fun getFeed(
        @QueryParam("cursor") cursor: Long?,
        @QueryParam("limit") @DefaultValue("20") limit: Int,
        @QueryParam("method") method: String?
    ): FeedResponse {
        return recipeService.getFeed(cursor, limit, method)
    }

    @POST
    @Authenticated
    fun createRecipe(req: CreateRecipeRequest): Response {
        val response = recipeService.createRecipe(authContext.memberId, req)
        return Response.status(Response.Status.CREATED).entity(response).build()
    }

    @GET
    @Path("/{id}")
    fun getRecipe(@PathParam("id") id: Long): RecipeResponse {
        return recipeService.getRecipe(id)
    }

    @PUT
    @Path("/{id}")
    @Authenticated
    fun updateRecipe(
        @PathParam("id") id: Long,
        req: UpdateRecipeRequest
    ): RecipeResponse {
        return recipeService.updateRecipe(authContext.memberId, id, req)
    }

    @DELETE
    @Path("/{id}")
    @Authenticated
    fun deleteRecipe(@PathParam("id") id: Long): Response {
        recipeService.deleteRecipe(authContext.memberId, id)
        return Response.noContent().build()
    }

    @POST
    @Path("/{id}/like")
    @Authenticated
    fun toggleLike(@PathParam("id") id: Long): Map<String, Any> {
        return recipeService.toggleLike(authContext.memberId, id)
    }

    @GET
    @Path("/{id}/comments")
    fun getComments(@PathParam("id") id: Long): List<CommentResponse> {
        return recipeService.getComments(id)
    }

    @POST
    @Path("/{id}/comments")
    @Authenticated
    fun addComment(
        @PathParam("id") id: Long,
        req: CommentRequest
    ): Response {
        val response = recipeService.addComment(authContext.memberId, id, req.content)
        return Response.status(Response.Status.CREATED).entity(response).build()
    }

    @DELETE
    @Path("/{id}/comments/{commentId}")
    @Authenticated
    fun deleteComment(
        @PathParam("id") id: Long,
        @PathParam("commentId") commentId: Long
    ): Response {
        recipeService.deleteComment(authContext.memberId, id, commentId)
        return Response.noContent().build()
    }
}
