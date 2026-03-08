package org.coffeezip.recipe

import io.quarkus.security.Authenticated
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import org.coffeezip.auth.AuthContext

@Path("/me/recipes")
@ApplicationScoped
@Authenticated
class MyRecipeResource {

    @Inject
    lateinit var recipeService: RecipeService

    @Inject
    lateinit var authContext: AuthContext

    @GET
    fun getMyRecipes(): List<RecipeResponse> {
        return recipeService.getMyRecipes(authContext.memberId)
    }
}
