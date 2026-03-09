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
@Table(name = "member")
class Member : PanacheEntityBase {

    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @get:Column(nullable = false, length = 20)
    var provider: String = ""

    @get:Column(name = "provider_id", nullable = false)
    var providerId: String = ""

    var email: String? = null

    @get:Column(nullable = false, length = 100)
    var nickname: String = ""

    @get:Column(name = "profile_image", length = 500)
    var profileImage: String? = null

    @get:Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
}
