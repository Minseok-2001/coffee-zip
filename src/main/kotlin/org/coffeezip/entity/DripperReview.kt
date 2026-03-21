package org.coffeezip.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "dripper_review",
    uniqueConstraints = [UniqueConstraint(columnNames = ["dripper_id", "member_id"])]
)
class DripperReview : PanacheEntityBase {

    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @get:Column(name = "dripper_id", nullable = false)
    var dripperId: Long = 0

    @get:Column(name = "member_id", nullable = false)
    var memberId: Long = 0

    var rating: Int = 0

    @get:Column(columnDefinition = "TEXT")
    var content: String? = null

    @get:Column(name = "extraction_rate")
    var extractionRate: Int? = null

    var cleanability: Int? = null

    var durability: Int? = null

    @get:Column(name = "heat_retention")
    var heatRetention: Int? = null

    @get:Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @get:Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
}
