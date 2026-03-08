package org.coffeezip.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "recipe_step")
class RecipeStep : PanacheEntity() {

    @get:Column(name = "recipe_id", nullable = false)
    var recipeId: Long = 0

    @get:Column(name = "step_order", nullable = false)
    var stepOrder: Int = 0

    @get:Column(nullable = false, length = 200)
    var label: String = ""

    @get:Column(nullable = false)
    var duration: Int = 0

    @get:Column(name = "water_amount")
    var waterAmount: Int? = null
}
