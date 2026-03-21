package org.coffeezip.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "dripper")
class Dripper : PanacheEntityBase {

    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @get:Column(nullable = false)
    var name: String = ""

    @get:Column(nullable = false)
    var brand: String = ""

    @get:Column(nullable = false)
    var type: String = ""

    @get:Column(nullable = false)
    var material: String = ""

    @get:Column(name = "holes_count")
    var holesCount: Int? = null

    @get:Column(name = "extraction_speed")
    var extractionSpeed: String? = null

    @get:Column(columnDefinition = "TEXT")
    var description: String? = null

    @get:Column(name = "created_by", nullable = false)
    var createdBy: Long = 0

    @get:Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @get:Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
}
