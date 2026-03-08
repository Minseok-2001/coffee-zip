package org.coffeezip.entity

import jakarta.persistence.*
import java.io.Serializable

data class RecipeTagId(
    val recipeId: Long = 0,
    val tag: String = ""
) : Serializable

@Entity
@Table(name = "recipe_tag")
@IdClass(RecipeTagId::class)
class RecipeTag {

    @get:Id
    @get:Column(name = "recipe_id")
    var recipeId: Long = 0

    @get:Id
    @get:Column(length = 50)
    var tag: String = ""
}
