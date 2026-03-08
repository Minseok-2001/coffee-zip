package org.coffeezip.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "daily_note")
class DailyNote : PanacheEntity() {

    @get:Column(name = "member_id", nullable = false)
    var memberId: Long = 0

    @get:Column(name = "note_date", nullable = false)
    var noteDate: LocalDate = LocalDate.now()

    @get:Column(columnDefinition = "TEXT")
    var content: String? = null

    var rating: Int? = null

    @get:Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @get:Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
}
