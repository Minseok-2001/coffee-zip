package org.coffeezip.recipe

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import org.coffeezip.entity.Recipe
import org.coffeezip.entity.RecipeComment
import org.coffeezip.entity.RecipeLike
import org.coffeezip.entity.RecipeStep

@ApplicationScoped
class RecipeRepository {

    @Inject
    lateinit var em: EntityManager

    fun findPublicFeed(cursor: Long?, limit: Int): List<Recipe> {
        val jpql = """
            SELECT r FROM Recipe r
            WHERE r.isPublic = true AND (:cursor IS NULL OR r.id < :cursor)
            ORDER BY r.id DESC
        """.trimIndent()
        return em.createQuery(jpql, Recipe::class.java)
            .setParameter("cursor", cursor)
            .setMaxResults(limit)
            .resultList
    }

    fun findByMemberId(memberId: Long): List<Recipe> {
        return em.createQuery(
            "SELECT r FROM Recipe r WHERE r.memberId = :memberId ORDER BY r.id DESC",
            Recipe::class.java
        )
            .setParameter("memberId", memberId)
            .resultList
    }

    fun findStepsByRecipeId(recipeId: Long): List<RecipeStep> {
        return em.createQuery(
            "SELECT s FROM RecipeStep s WHERE s.recipeId = :recipeId ORDER BY s.stepOrder ASC",
            RecipeStep::class.java
        )
            .setParameter("recipeId", recipeId)
            .resultList
    }

    fun findTagsByRecipeId(recipeId: Long): List<String> {
        return em.createQuery(
            "SELECT t.tag FROM RecipeTag t WHERE t.recipeId = :recipeId",
            String::class.java
        )
            .setParameter("recipeId", recipeId)
            .resultList
    }

    fun findCommentsByRecipeId(recipeId: Long): List<RecipeComment> {
        return em.createQuery(
            "SELECT c FROM RecipeComment c WHERE c.recipeId = :recipeId ORDER BY c.id ASC",
            RecipeComment::class.java
        )
            .setParameter("recipeId", recipeId)
            .resultList
    }

    fun existsLike(recipeId: Long, memberId: Long): Boolean {
        val count = em.createQuery(
            "SELECT COUNT(l) FROM RecipeLike l WHERE l.recipeId = :recipeId AND l.memberId = :memberId",
            Long::class.javaObjectType
        )
            .setParameter("recipeId", recipeId)
            .setParameter("memberId", memberId)
            .singleResult
        return count > 0
    }

    fun deleteLike(recipeId: Long, memberId: Long) {
        em.createQuery(
            "DELETE FROM RecipeLike l WHERE l.recipeId = :recipeId AND l.memberId = :memberId"
        )
            .setParameter("recipeId", recipeId)
            .setParameter("memberId", memberId)
            .executeUpdate()
    }
}
