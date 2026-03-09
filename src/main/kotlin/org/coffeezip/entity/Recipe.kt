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
@Table(name = "recipe")
class Recipe : PanacheEntityBase {

    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @get:Column(name = "member_id", nullable = false)
    var memberId: Long = 0

    @get:Column(nullable = false, length = 200)
    var title: String = ""

    @get:Column(columnDefinition = "TEXT")
    var description: String? = null

    @get:Column(name = "coffee_bean", length = 200)
    var coffeeBean: String? = null

    @get:Column(length = 100)
    var origin: String? = null

    @get:Column(name = "roast_level", length = 50)
    var roastLevel: String? = null

    @get:Column(length = 100)
    var grinder: String? = null

    @get:Column(name = "grind_size", length = 50)
    var grindSize: String? = null

    @get:Column(name = "coffee_grams")
    var coffeeGrams: Double? = null

    @get:Column(name = "water_grams")
    var waterGrams: Double? = null

    @get:Column(name = "water_temp")
    var waterTemp: Int? = null

    @get:Column(name = "target_yield")
    var targetYield: Int? = null

    @get:Column(name = "published_at")
    var publishedAt: LocalDateTime? = LocalDateTime.now()

    @get:Column(name = "like_count", nullable = false)
    var likeCount: Int = 0

    @get:Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @get:Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
}
