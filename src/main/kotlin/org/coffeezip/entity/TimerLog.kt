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
@Table(name = "timer_log")
class TimerLog : PanacheEntityBase {

    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @get:Column(name = "member_id", nullable = false)
    var memberId: Long = 0

    @get:Column(name = "note_id")
    var noteId: Long? = null

    @get:Column(name = "recipe_id", nullable = false)
    var recipeId: Long = 0

    @get:Column(name = "recipe_name", nullable = false, length = 200)
    var recipeName: String = ""

    @get:Column(name = "started_at", nullable = false)
    var startedAt: LocalDateTime = LocalDateTime.now()

    @get:Column(name = "completed_at")
    var completedAt: LocalDateTime? = null
}
