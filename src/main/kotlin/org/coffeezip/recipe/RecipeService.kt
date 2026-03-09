package org.coffeezip.recipe

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.ws.rs.WebApplicationException
import org.coffeezip.entity.Recipe
import org.coffeezip.entity.RecipeComment
import org.coffeezip.entity.RecipeLike
import org.coffeezip.entity.RecipeStep
import org.coffeezip.entity.RecipeTag

@ApplicationScoped
class RecipeService {

    @Inject
    lateinit var recipeRepository: RecipeRepository

    @Inject
    lateinit var em: EntityManager

    fun getFeed(cursor: Long?, limit: Int): FeedResponse {
        val results = recipeRepository.findPublicFeed(cursor, limit + 1)
        val hasNext = results.size > limit
        val items = if (hasNext) results.dropLast(1) else results
        val nextCursor = if (hasNext) items.last().id else null
        return FeedResponse(
            items = items.map { toRecipeResponse(it) },
            nextCursor = nextCursor
        )
    }

    @Transactional
    fun createRecipe(memberId: Long, req: CreateRecipeRequest): RecipeResponse {
        val recipe = Recipe().apply {
            this.memberId = memberId
            title = req.title
            description = req.description
            coffeeBean = req.coffeeBean
            origin = req.origin
            roastLevel = req.roastLevel
            grinder = req.grinder
            grindSize = req.grindSize
            coffeeGrams = req.coffeeGrams
            waterGrams = req.waterGrams
            waterTemp = req.waterTemp
            targetYield = req.targetYield
            publishedAt = if (req.isPublic) java.time.LocalDateTime.now() else null
            likeCount = 0
        }
        em.persist(recipe)
        em.flush()

        req.steps.forEach { stepReq ->
            val step = RecipeStep().apply {
                recipeId = recipe.id!!
                stepOrder = stepReq.stepOrder
                label = stepReq.label
                duration = stepReq.duration
                waterAmount = stepReq.waterAmount
            }
            em.persist(step)
        }

        req.tags.forEach { tagStr ->
            val tag = RecipeTag().apply {
                recipeId = recipe.id!!
                tag = tagStr
            }
            em.persist(tag)
        }

        em.flush()
        return toRecipeResponse(recipe, req.steps.mapIndexed { _, stepReq ->
            RecipeStepResponse(
                id = 0L,
                stepOrder = stepReq.stepOrder,
                label = stepReq.label,
                duration = stepReq.duration,
                waterAmount = stepReq.waterAmount
            )
        }, req.tags)
    }

    fun getRecipe(id: Long): RecipeResponse {
        val recipe = em.find(Recipe::class.java, id) ?: throw WebApplicationException(404)
        return toRecipeResponse(recipe)
    }

    @Transactional
    fun updateRecipe(memberId: Long, id: Long, req: UpdateRecipeRequest): RecipeResponse {
        val recipe = em.find(Recipe::class.java, id) ?: throw WebApplicationException(404)
        if (recipe.memberId != memberId) throw WebApplicationException(403)

        recipe.title = req.title
        recipe.description = req.description
        recipe.coffeeBean = req.coffeeBean
        recipe.origin = req.origin
        recipe.roastLevel = req.roastLevel
        recipe.grinder = req.grinder
        recipe.grindSize = req.grindSize
        recipe.coffeeGrams = req.coffeeGrams
        recipe.waterGrams = req.waterGrams
        recipe.waterTemp = req.waterTemp
        recipe.targetYield = req.targetYield
        recipe.publishedAt = if (req.isPublic) (recipe.publishedAt ?: java.time.LocalDateTime.now()) else null
        recipe.updatedAt = java.time.LocalDateTime.now()

        em.createQuery("DELETE FROM RecipeStep s WHERE s.recipeId = :recipeId")
            .setParameter("recipeId", id)
            .executeUpdate()
        em.createQuery("DELETE FROM RecipeTag t WHERE t.recipeId = :recipeId")
            .setParameter("recipeId", id)
            .executeUpdate()

        req.steps.forEach { stepReq ->
            val step = RecipeStep().apply {
                recipeId = id
                stepOrder = stepReq.stepOrder
                label = stepReq.label
                duration = stepReq.duration
                waterAmount = stepReq.waterAmount
            }
            em.persist(step)
        }

        req.tags.forEach { tagStr ->
            val tag = RecipeTag().apply {
                recipeId = id
                tag = tagStr
            }
            em.persist(tag)
        }

        em.flush()
        return toRecipeResponse(recipe)
    }

    @Transactional
    fun deleteRecipe(memberId: Long, id: Long) {
        val recipe = em.find(Recipe::class.java, id) ?: throw WebApplicationException(404)
        if (recipe.memberId != memberId) throw WebApplicationException(403)

        em.createQuery("DELETE FROM RecipeStep s WHERE s.recipeId = :recipeId")
            .setParameter("recipeId", id)
            .executeUpdate()
        em.createQuery("DELETE FROM RecipeTag t WHERE t.recipeId = :recipeId")
            .setParameter("recipeId", id)
            .executeUpdate()
        em.createQuery("DELETE FROM RecipeLike l WHERE l.recipeId = :recipeId")
            .setParameter("recipeId", id)
            .executeUpdate()
        em.createQuery("DELETE FROM RecipeComment c WHERE c.recipeId = :recipeId")
            .setParameter("recipeId", id)
            .executeUpdate()

        em.remove(recipe)
    }

    @Transactional
    fun toggleLike(memberId: Long, recipeId: Long): Map<String, Any> {
        val recipe = em.find(Recipe::class.java, recipeId) ?: throw WebApplicationException(404)
        val liked = recipeRepository.existsLike(recipeId, memberId)

        return if (liked) {
            recipeRepository.deleteLike(recipeId, memberId)
            recipe.likeCount = maxOf(0, recipe.likeCount - 1)
            mapOf("liked" to false, "likeCount" to recipe.likeCount)
        } else {
            val like = RecipeLike().apply {
                this.recipeId = recipeId
                this.memberId = memberId
            }
            em.persist(like)
            recipe.likeCount = recipe.likeCount + 1
            mapOf("liked" to true, "likeCount" to recipe.likeCount)
        }
    }

    fun getComments(recipeId: Long): List<CommentResponse> {
        em.find(Recipe::class.java, recipeId) ?: throw WebApplicationException(404)
        return recipeRepository.findCommentsByRecipeId(recipeId).map { toCommentResponse(it) }
    }

    @Transactional
    fun addComment(memberId: Long, recipeId: Long, content: String): CommentResponse {
        em.find(Recipe::class.java, recipeId) ?: throw WebApplicationException(404)
        val comment = RecipeComment().apply {
            this.recipeId = recipeId
            this.memberId = memberId
            this.content = content
        }
        em.persist(comment)
        em.flush()
        return toCommentResponse(comment)
    }

    @Transactional
    fun deleteComment(memberId: Long, recipeId: Long, commentId: Long) {
        val comment = em.find(RecipeComment::class.java, commentId) ?: throw WebApplicationException(404)
        if (comment.recipeId != recipeId) throw WebApplicationException(404)
        if (comment.memberId != memberId) throw WebApplicationException(403)
        em.remove(comment)
    }

    fun getMyRecipes(memberId: Long): List<RecipeResponse> {
        return recipeRepository.findByMemberId(memberId).map { toRecipeResponse(it) }
    }

    private fun toRecipeResponse(
        recipe: Recipe,
        steps: List<RecipeStepResponse>? = null,
        tags: List<String>? = null
    ): RecipeResponse {
        val resolvedSteps = steps ?: recipeRepository.findStepsByRecipeId(recipe.id!!).map {
            RecipeStepResponse(
                id = it.id!!,
                stepOrder = it.stepOrder,
                label = it.label,
                duration = it.duration,
                waterAmount = it.waterAmount
            )
        }
        val resolvedTags = tags ?: recipeRepository.findTagsByRecipeId(recipe.id!!)
        return RecipeResponse(
            id = recipe.id!!,
            memberId = recipe.memberId,
            title = recipe.title,
            description = recipe.description,
            coffeeBean = recipe.coffeeBean,
            origin = recipe.origin,
            roastLevel = recipe.roastLevel,
            grinder = recipe.grinder,
            grindSize = recipe.grindSize,
            coffeeGrams = recipe.coffeeGrams,
            waterGrams = recipe.waterGrams,
            waterTemp = recipe.waterTemp,
            targetYield = recipe.targetYield,
            publishedAt = recipe.publishedAt?.toString(),
            likeCount = recipe.likeCount,
            steps = resolvedSteps,
            tags = resolvedTags,
            createdAt = recipe.createdAt.toString(),
            updatedAt = recipe.updatedAt.toString()
        )
    }

    private fun toCommentResponse(comment: RecipeComment): CommentResponse {
        return CommentResponse(
            id = comment.id!!,
            memberId = comment.memberId,
            content = comment.content,
            createdAt = comment.createdAt.toString()
        )
    }
}
