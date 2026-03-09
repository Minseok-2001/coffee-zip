package org.coffeezip.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "recipe_comment")
class RecipeComment : PanacheEntityBase {

    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @get:Column(name = "recipe_id", nullable = false)
    var recipeId: Long = 0

    @get:Column(name = "member_id", nullable = false)
    var memberId: Long = 0

    @get:Column(nullable = false, columnDefinition = "TEXT")
    var content: String = ""

    @get:Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
}
