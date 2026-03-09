package org.coffeezip.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "recipe_step")
class RecipeStep : PanacheEntityBase {

    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

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
