package org.coffeezip.dripper

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.WebApplicationException
import org.coffeezip.dto.*
import org.coffeezip.entity.Dripper
import org.coffeezip.entity.DripperReview
import java.time.LocalDateTime

@ApplicationScoped
class DripperService {

    @Inject
    lateinit var repo: DripperRepository

    @Inject
    lateinit var em: EntityManager

    fun getDrippers(
        search: String?,
        type: String?,
        material: String?,
        page: Int,
        size: Int
    ): List<DripperSummaryResponse> {
        val drippers = repo.findDrippers(search, type, material, page, size)
        if (drippers.isEmpty()) return emptyList()
        val ids = drippers.map { it.id!! }
        val reviews = repo.findReviewsByDripperIds(ids)
        val reviewsByDripper = reviews.groupBy { it.dripperId }
        return drippers.map { d ->
            val dr = reviewsByDripper[d.id] ?: emptyList()
            DripperSummaryResponse(
                id = d.id!!,
                name = d.name,
                brand = d.brand,
                type = d.type,
                material = d.material,
                extractionSpeed = d.extractionSpeed,
                avgRating = if (dr.isEmpty()) null else dr.map { it.rating }.average(),
                reviewCount = dr.size
            )
        }
    }

    fun getDripperDetail(id: Long): DripperDetailResponse {
        val dripper = em.find(Dripper::class.java, id)
            ?: throw NotFoundException("Dripper not found")
        val reviews = repo.findReviewsByDripperId(id)
        return toDripperDetailResponse(dripper, reviews)
    }

    fun getDripperSummaries(ids: List<Long>): Map<Long, DripperSummaryResponse> {
        if (ids.isEmpty()) return emptyMap()
        val drippers = repo.findDrippersByIds(ids)
        val reviews = repo.findReviewsByDripperIds(ids)
        val reviewsByDripper = reviews.groupBy { it.dripperId }
        return drippers.associate { d ->
            val dr = reviewsByDripper[d.id] ?: emptyList()
            d.id!! to DripperSummaryResponse(
                id = d.id!!,
                name = d.name,
                brand = d.brand,
                type = d.type,
                material = d.material,
                extractionSpeed = d.extractionSpeed,
                avgRating = if (dr.isEmpty()) null else dr.map { it.rating }.average(),
                reviewCount = dr.size
            )
        }
    }

    @Transactional
    fun createDripper(req: CreateDripperRequest, memberId: Long): Long {
        val dripper = Dripper().apply {
            name = req.name
            brand = req.brand
            type = req.type
            material = req.material
            holesCount = req.holesCount
            extractionSpeed = req.extractionSpeed
            description = req.description
            createdBy = memberId
            createdAt = LocalDateTime.now()
            updatedAt = LocalDateTime.now()
        }
        em.persist(dripper)
        em.flush()
        return dripper.id!!
    }

    @Transactional
    fun updateDripper(id: Long, req: UpdateDripperRequest, memberId: Long): DripperDetailResponse {
        val dripper = em.find(Dripper::class.java, id)
            ?: throw NotFoundException("Dripper not found")
        if (dripper.createdBy != memberId) throw WebApplicationException(403)

        req.name?.let { dripper.name = it }
        req.brand?.let { dripper.brand = it }
        req.type?.let { dripper.type = it }
        req.material?.let { dripper.material = it }
        dripper.holesCount = req.holesCount
        dripper.extractionSpeed = req.extractionSpeed
        dripper.description = req.description
        dripper.updatedAt = LocalDateTime.now()

        val reviews = repo.findReviewsByDripperId(id)
        return toDripperDetailResponse(dripper, reviews)
    }

    @Transactional
    fun upsertReview(dripperId: Long, req: UpsertDripperReviewRequest, memberId: Long) {
        em.find(Dripper::class.java, dripperId)
            ?: throw NotFoundException("Dripper not found")
        val existing = repo.findReviewByDripperAndMember(dripperId, memberId)
        if (existing != null) {
            existing.rating = req.rating
            existing.content = req.content
            existing.extractionRate = req.extractionRate
            existing.cleanability = req.cleanability
            existing.durability = req.durability
            existing.heatRetention = req.heatRetention
            existing.updatedAt = LocalDateTime.now()
        } else {
            val review = DripperReview().apply {
                this.dripperId = dripperId
                this.memberId = memberId
                rating = req.rating
                content = req.content
                extractionRate = req.extractionRate
                cleanability = req.cleanability
                durability = req.durability
                heatRetention = req.heatRetention
                createdAt = LocalDateTime.now()
                updatedAt = LocalDateTime.now()
            }
            em.persist(review)
        }
    }

    @Transactional
    fun deleteReview(dripperId: Long, memberId: Long) {
        val review = repo.findReviewByDripperAndMember(dripperId, memberId)
            ?: throw NotFoundException("Review not found")
        em.remove(review)
    }

    fun getReviews(dripperId: Long, page: Int, size: Int): List<DripperReviewResponse> =
        repo.findReviewsByDripperIdPaged(dripperId, page, size).map { it.toResponse() }

    private fun toDripperDetailResponse(dripper: Dripper, reviews: List<DripperReview>): DripperDetailResponse {
        val recipeCount = em.createQuery(
            "SELECT COUNT(r) FROM Recipe r WHERE r.dripperId = :dripperId", Long::class.java
        ).setParameter("dripperId", dripper.id!!).singleResult.toInt()

        return DripperDetailResponse(
            id = dripper.id!!,
            name = dripper.name,
            brand = dripper.brand,
            type = dripper.type,
            material = dripper.material,
            holesCount = dripper.holesCount,
            extractionSpeed = dripper.extractionSpeed,
            description = dripper.description,
            avgRating = if (reviews.isEmpty()) null else reviews.map { it.rating }.average(),
            reviewCount = reviews.size,
            recipeCount = recipeCount,
            avgExtractionRate = reviews.mapNotNull { it.extractionRate }.takeIf { it.isNotEmpty() }?.average(),
            avgCleanability = reviews.mapNotNull { it.cleanability }.takeIf { it.isNotEmpty() }?.average(),
            avgDurability = reviews.mapNotNull { it.durability }.takeIf { it.isNotEmpty() }?.average(),
            avgHeatRetention = reviews.mapNotNull { it.heatRetention }.takeIf { it.isNotEmpty() }?.average(),
            createdBy = dripper.createdBy,
            createdAt = dripper.createdAt
        )
    }

    private fun DripperReview.toResponse() = DripperReviewResponse(
        id = id!!,
        dripperId = dripperId,
        memberId = memberId,
        rating = rating,
        content = content,
        extractionRate = extractionRate,
        cleanability = cleanability,
        durability = durability,
        heatRetention = heatRetention,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
