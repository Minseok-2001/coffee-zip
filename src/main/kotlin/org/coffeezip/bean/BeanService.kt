package org.coffeezip.bean

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.ws.rs.NotFoundException
import org.coffeezip.dto.BeanDetailResponse
import org.coffeezip.dto.BeanReviewResponse
import org.coffeezip.dto.BeanSummaryResponse
import org.coffeezip.dto.CreateBeanRequest
import org.coffeezip.dto.UpdateBeanRequest
import org.coffeezip.dto.UpsertBeanReviewRequest
import org.coffeezip.entity.Bean
import org.coffeezip.entity.BeanReview
import java.time.LocalDateTime

@ApplicationScoped
class BeanService {

    @Inject
    lateinit var beanRepository: BeanRepository

    @Inject
    lateinit var em: EntityManager

    fun getBeans(
        search: String?,
        origin: String?,
        roastLevel: String?,
        page: Int,
        size: Int
    ): List<BeanSummaryResponse> {
        val beans = beanRepository.findBeans(search, origin, roastLevel, page, size)
        if (beans.isEmpty()) return emptyList()
        val beanIds = beans.map { it.id!! }
        val reviews = beanRepository.findReviewsByBeanIds(beanIds)
        val reviewsByBeanId = reviews.groupBy { it.beanId }
        return beans.map { bean ->
            val beanReviews = reviewsByBeanId[bean.id] ?: emptyList()
            toBeanSummaryResponse(bean, beanReviews)
        }
    }

    fun getBeanDetail(beanId: Long): BeanDetailResponse {
        val bean = em.find(Bean::class.java, beanId)
            ?: throw NotFoundException("Bean not found: $beanId")
        val reviews = beanRepository.findReviewsByBeanId(beanId)
        return toBeanDetailResponse(bean, reviews)
    }

    fun getBeanSummaries(beanIds: List<Long>): Map<Long, BeanSummaryResponse> {
        if (beanIds.isEmpty()) return emptyMap()
        val beans = beanRepository.findBeansByIds(beanIds)
        val reviews = beanRepository.findReviewsByBeanIds(beanIds)
        val reviewsByBeanId = reviews.groupBy { it.beanId }
        return beans.associate { bean ->
            val beanReviews = reviewsByBeanId[bean.id] ?: emptyList()
            bean.id!! to toBeanSummaryResponse(bean, beanReviews)
        }
    }

    @Transactional
    fun createBean(request: CreateBeanRequest, memberId: Long): BeanDetailResponse {
        val bean = Bean().apply {
            name = request.name
            roastery = request.roastery
            origin = request.origin
            region = request.region
            farm = request.farm
            variety = request.variety
            processing = request.processing
            roastLevel = request.roastLevel
            altitude = request.altitude
            harvestYear = request.harvestYear
            description = request.description
            flavorNotes = request.flavorNotes.toMutableSet()
            createdBy = memberId
            createdAt = LocalDateTime.now()
            updatedAt = LocalDateTime.now()
        }
        em.persist(bean)
        em.flush()
        return toBeanDetailResponse(bean, emptyList())
    }

    @Transactional
    fun updateBean(beanId: Long, request: UpdateBeanRequest, memberId: Long): BeanDetailResponse {
        val bean = em.find(Bean::class.java, beanId)
            ?: throw NotFoundException("Bean not found: $beanId")
        if (bean.createdBy != memberId) throw jakarta.ws.rs.WebApplicationException(403)
        // Required fields: null means "don't update"
        request.name?.let { bean.name = it }
        request.roastery?.let { bean.roastery = it }
        request.origin?.let { bean.origin = it }
        request.roastLevel?.let { bean.roastLevel = it }
        // Optional nullable fields: always apply — null clears the field
        bean.region = request.region
        bean.farm = request.farm
        bean.variety = request.variety
        bean.processing = request.processing
        bean.altitude = request.altitude
        bean.harvestYear = request.harvestYear
        bean.description = request.description
        // flavorNotes: null means "don't change the notes"; non-null replaces the set
        request.flavorNotes?.let { bean.flavorNotes = it.toMutableSet() }
        bean.updatedAt = LocalDateTime.now()
        em.flush()
        val reviews = beanRepository.findReviewsByBeanId(beanId)
        return toBeanDetailResponse(bean, reviews)
    }

    @Transactional
    fun upsertReview(beanId: Long, request: UpsertBeanReviewRequest, memberId: Long): BeanReviewResponse {
        em.find(Bean::class.java, beanId)
            ?: throw NotFoundException("Bean not found: $beanId")
        val existing = beanRepository.findReviewByBeanAndMember(beanId, memberId)
        val review = if (existing != null) {
            existing.rating = request.rating
            existing.content = request.content
            existing.acidity = request.acidity
            existing.sweetness = request.sweetness
            existing.body = request.body
            existing.aroma = request.aroma
            existing.updatedAt = LocalDateTime.now()
            em.flush()
            existing
        } else {
            val newReview = BeanReview().apply {
                this.beanId = beanId
                this.memberId = memberId
                rating = request.rating
                content = request.content
                acidity = request.acidity
                sweetness = request.sweetness
                body = request.body
                aroma = request.aroma
                createdAt = LocalDateTime.now()
                updatedAt = LocalDateTime.now()
            }
            em.persist(newReview)
            em.flush()
            newReview
        }
        return toBeanReviewResponse(review)
    }

    @Transactional
    fun deleteReview(beanId: Long, memberId: Long) {
        val review = beanRepository.findReviewByBeanAndMember(beanId, memberId)
            ?: throw NotFoundException("Review not found for bean $beanId and member $memberId")
        em.remove(review)
    }

    fun getReviews(beanId: Long, page: Int, size: Int): List<BeanReviewResponse> {
        em.find(Bean::class.java, beanId)
            ?: throw NotFoundException("Bean not found: $beanId")
        return beanRepository.findReviewsByBeanIdPaged(beanId, page, size).map { toBeanReviewResponse(it) }
    }

    private fun toBeanSummaryResponse(bean: Bean, reviews: List<BeanReview>): BeanSummaryResponse {
        val avgRating = if (reviews.isEmpty()) null else reviews.map { it.rating }.average()
        return BeanSummaryResponse(
            id = bean.id!!,
            name = bean.name,
            roastery = bean.roastery,
            origin = bean.origin,
            roastLevel = bean.roastLevel,
            flavorNotes = bean.flavorNotes.toList(),
            avgRating = avgRating,
            reviewCount = reviews.size
        )
    }

    private fun toBeanDetailResponse(bean: Bean, reviews: List<BeanReview>): BeanDetailResponse {
        val avgRating = if (reviews.isEmpty()) null else reviews.map { it.rating }.average()
        val reviewCount = reviews.size
        val acidityValues = reviews.mapNotNull { it.acidity }
        val sweetnessValues = reviews.mapNotNull { it.sweetness }
        val bodyValues = reviews.mapNotNull { it.body }
        val aromaValues = reviews.mapNotNull { it.aroma }
        return BeanDetailResponse(
            id = bean.id!!,
            name = bean.name,
            roastery = bean.roastery,
            origin = bean.origin,
            region = bean.region,
            farm = bean.farm,
            variety = bean.variety,
            processing = bean.processing,
            roastLevel = bean.roastLevel,
            altitude = bean.altitude,
            harvestYear = bean.harvestYear,
            description = bean.description,
            flavorNotes = bean.flavorNotes.toList(),
            avgRating = avgRating,
            reviewCount = reviewCount,
            recipeCount = (em.createQuery(
                "SELECT COUNT(r) FROM Recipe r WHERE r.beanId = :beanId",
                Long::class.java
            ).setParameter("beanId", bean.id!!).singleResult).toInt(),
            avgAcidity = if (acidityValues.isEmpty()) null else acidityValues.average(),
            avgSweetness = if (sweetnessValues.isEmpty()) null else sweetnessValues.average(),
            avgBody = if (bodyValues.isEmpty()) null else bodyValues.average(),
            avgAroma = if (aromaValues.isEmpty()) null else aromaValues.average(),
            createdBy = bean.createdBy,
            createdAt = bean.createdAt
        )
    }

    private fun toBeanReviewResponse(review: BeanReview): BeanReviewResponse {
        return BeanReviewResponse(
            id = review.id!!,
            beanId = review.beanId,
            memberId = review.memberId,
            rating = review.rating,
            content = review.content,
            acidity = review.acidity,
            sweetness = review.sweetness,
            body = review.body,
            aroma = review.aroma,
            createdAt = review.createdAt,
            updatedAt = review.updatedAt
        )
    }
}
