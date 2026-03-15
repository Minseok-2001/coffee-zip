package org.coffeezip.recipe

import io.quarkus.security.Authenticated
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.coffeezip.auth.AuthContext

@Path("/me/recipes")
@ApplicationScoped
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class MyRecipeResource {

    @Inject
    lateinit var recipeService: RecipeService

    @Inject
    lateinit var authContext: AuthContext

    @GET
    fun getMyRecipes(): List<RecipeResponse> {
        return recipeService.getMyRecipes(authContext.memberId)
    }

    @POST
    fun createRecipe(req: CreateRecipeRequest): Response {
        val recipe = recipeService.createRecipe(authContext.memberId, req)
        return Response.status(Response.Status.CREATED).entity(recipe).build()
    }

    @PUT
    @Path("/{id}")
    fun updateRecipe(@PathParam("id") id: Long, req: UpdateRecipeRequest): RecipeResponse {
        return recipeService.updateRecipe(authContext.memberId, id, req)
    }

    @DELETE
    @Path("/{id}")
    fun deleteRecipe(@PathParam("id") id: Long): Response {
        recipeService.deleteRecipe(authContext.memberId, id)
        return Response.noContent().build()
    }
}
