package org.coffeezip.dripper

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import org.coffeezip.entity.Dripper
import org.coffeezip.entity.DripperReview

@ApplicationScoped
class DripperRepository {

    @Inject
    lateinit var em: EntityManager

    fun findDrippers(
        search: String?,
        type: String?,
        material: String?,
        page: Int,
        size: Int
    ): List<Dripper> {
        val conditions = mutableListOf<String>()
        if (!search.isNullOrBlank()) {
            conditions.add("(LOWER(d.name) LIKE :search OR LOWER(d.brand) LIKE :search)")
        }
        if (!type.isNullOrBlank()) conditions.add("d.type = :type")
        if (!material.isNullOrBlank()) conditions.add("d.material = :material")

        val where = if (conditions.isEmpty()) "" else "WHERE ${conditions.joinToString(" AND ")}"
        val jpql = "SELECT d FROM Dripper d $where ORDER BY d.id DESC"

        val query = em.createQuery(jpql, Dripper::class.java)
        if (!search.isNullOrBlank()) query.setParameter("search", "%${search.lowercase()}%")
        if (!type.isNullOrBlank()) query.setParameter("type", type)
        if (!material.isNullOrBlank()) query.setParameter("material", material)

        return query
            .setFirstResult(page * size)
            .setMaxResults(size)
            .resultList
    }

    fun findReviewsByDripperId(dripperId: Long): List<DripperReview> =
        em.createQuery(
            "SELECT r FROM DripperReview r WHERE r.dripperId = :dripperId ORDER BY r.id DESC",
            DripperReview::class.java
        ).setParameter("dripperId", dripperId).resultList

    fun findReviewsByDripperIdPaged(dripperId: Long, page: Int, size: Int): List<DripperReview> =
        em.createQuery(
            "SELECT r FROM DripperReview r WHERE r.dripperId = :dripperId ORDER BY r.id DESC",
            DripperReview::class.java
        ).setParameter("dripperId", dripperId)
            .setFirstResult(page * size)
            .setMaxResults(size)
            .resultList

    fun findReviewByDripperAndMember(dripperId: Long, memberId: Long): DripperReview? =
        em.createQuery(
            "SELECT r FROM DripperReview r WHERE r.dripperId = :dripperId AND r.memberId = :memberId",
            DripperReview::class.java
        ).setParameter("dripperId", dripperId)
            .setParameter("memberId", memberId)
            .resultList
            .firstOrNull()

    fun findDrippersByIds(ids: List<Long>): List<Dripper> {
        if (ids.isEmpty()) return emptyList()
        return em.createQuery(
            "SELECT d FROM Dripper d WHERE d.id IN :ids",
            Dripper::class.java
        ).setParameter("ids", ids).resultList
    }

    fun findReviewsByDripperIds(ids: List<Long>): List<DripperReview> {
        if (ids.isEmpty()) return emptyList()
        return em.createQuery(
            "SELECT r FROM DripperReview r WHERE r.dripperId IN :ids",
            DripperReview::class.java
        ).setParameter("ids", ids).resultList
    }
}
