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

    var name: String = ""
    var roastery: String = ""
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
    var flavorNotes: MutableList<String> = mutableListOf()

    @get:Column(name = "created_by")
    var createdBy: Long = 0

    @get:Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now()

    @get:Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
}
