package org.coffeezip.bean

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import org.coffeezip.entity.Bean
import org.coffeezip.entity.BeanReview

@ApplicationScoped
class BeanRepository {

    @Inject
    lateinit var em: EntityManager

    fun findBeans(search: String?, origin: String?, roastLevel: String?, page: Int, size: Int): List<Bean> {
        val conditions = mutableListOf<String>()
        if (!search.isNullOrBlank()) {
            conditions.add("(LOWER(b.name) LIKE :search OR LOWER(b.roastery) LIKE :search OR LOWER(b.origin) LIKE :search)")
        }
        if (!origin.isNullOrBlank()) {
            conditions.add("b.origin = :origin")
        }
        if (!roastLevel.isNullOrBlank()) {
            conditions.add("b.roastLevel = :roastLevel")
        }
        val where = if (conditions.isEmpty()) "" else "WHERE " + conditions.joinToString(" AND ")
        val jpql = "SELECT b FROM Bean b $where ORDER BY b.id DESC"
        val query = em.createQuery(jpql, Bean::class.java)
        if (!search.isNullOrBlank()) {
            query.setParameter("search", "%${search.lowercase()}%")
        }
        if (!origin.isNullOrBlank()) {
            query.setParameter("origin", origin)
        }
        if (!roastLevel.isNullOrBlank()) {
            query.setParameter("roastLevel", roastLevel)
        }
        return query
            .setFirstResult(page * size)
            .setMaxResults(size)
            .resultList
    }

    fun findReviewsByBeanId(beanId: Long): List<BeanReview> {
        return em.createQuery(
            "SELECT r FROM BeanReview r WHERE r.beanId = :beanId ORDER BY r.id DESC",
            BeanReview::class.java
        )
            .setParameter("beanId", beanId)
            .resultList
    }

    fun findReviewsByBeanIdPaged(beanId: Long, page: Int, size: Int): List<BeanReview> {
        return em.createQuery(
            "SELECT r FROM BeanReview r WHERE r.beanId = :beanId ORDER BY r.id DESC",
            BeanReview::class.java
        )
            .setParameter("beanId", beanId)
            .setFirstResult(page * size)
            .setMaxResults(size)
            .resultList
    }

    fun findReviewByBeanAndMember(beanId: Long, memberId: Long): BeanReview? {
        return em.createQuery(
            "SELECT r FROM BeanReview r WHERE r.beanId = :beanId AND r.memberId = :memberId",
            BeanReview::class.java
        )
            .setParameter("beanId", beanId)
            .setParameter("memberId", memberId)
            .resultList
            .firstOrNull()
    }

    fun findBeansByIds(ids: List<Long>): List<Bean> {
        if (ids.isEmpty()) return emptyList()
        return em.createQuery(
            "SELECT b FROM Bean b WHERE b.id IN :ids",
            Bean::class.java
        )
            .setParameter("ids", ids)
            .resultList
    }

    fun findReviewsByBeanIds(ids: List<Long>): List<BeanReview> {
        if (ids.isEmpty()) return emptyList()
        return em.createQuery(
            "SELECT r FROM BeanReview r WHERE r.beanId IN :ids",
            BeanReview::class.java
        )
            .setParameter("ids", ids)
            .resultList
    }
}
