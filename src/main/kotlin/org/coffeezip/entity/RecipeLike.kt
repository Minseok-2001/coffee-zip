package org.coffeezip.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime

data class RecipeLikeId(
    val recipeId: Long = 0,
    val memberId: Long = 0
) : Serializable

@Entity
@Table(name = "recipe_like")
@IdClass(RecipeLikeId::class)
class RecipeLike {

    @get:Id
    @get:Column(name = "recipe_id")
    var recipeId: Long = 0

    @get:Id
    @get:Column(name = "member_id")
    var memberId: Long = 0

    @get:Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
}
