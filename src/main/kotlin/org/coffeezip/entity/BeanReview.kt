package org.coffeezip.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "bean_review",
    uniqueConstraints = [UniqueConstraint(columnNames = ["bean_id", "member_id"])]
)
class BeanReview : PanacheEntityBase {

    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @get:Column(name = "bean_id", nullable = false)
    var beanId: Long = 0

    @get:Column(name = "member_id", nullable = false)
    var memberId: Long = 0

    var rating: Int = 0

    @get:Column(columnDefinition = "TEXT")
    var content: String? = null

    var acidity: Int? = null
    var sweetness: Int? = null
    var body: Int? = null
    var aroma: Int? = null

    @get:Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @get:Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
}
