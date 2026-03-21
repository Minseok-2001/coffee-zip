package org.coffeezip.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "bean")
class Bean : PanacheEntityBase {

    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @get:Column(nullable = false)
    var name: String = ""

    @get:Column(nullable = false)
    var roastery: String = ""

    @get:Column(nullable = false)
    var origin: String = ""
    var region: String? = null
    var farm: String? = null
    var variety: String? = null
    var processing: String? = null

    @get:Column(name = "roast_level")
    var roastLevel: String = ""

    var altitude: Int? = null

    @get:Column(name = "harvest_year")
    var harvestYear: Int? = null

    var description: String? = null

    @get:ElementCollection(fetch = FetchType.EAGER)
    @get:CollectionTable(name = "bean_flavor_note", joinColumns = [JoinColumn(name = "bean_id")])
    @get:Column(name = "note", length = 50)
    var flavorNotes: MutableSet<String> = mutableSetOf()

    @get:Column(name = "created_by", nullable = false)
    var createdBy: Long = 0

    @get:Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @get:Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
}
