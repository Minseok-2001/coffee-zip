package org.coffeezip.recipe

import org.coffeezip.dto.BeanSummaryResponse
import org.coffeezip.dto.DripperSummaryResponse

data class RecipeStepRequest(
    val stepOrder: Int,
    val label: String,
    val duration: Int,
    val waterAmount: Int?
)

data class CreateRecipeRequest(
    val title: String,
    val description: String?,
    val method: String?,
    val coffeeBean: String?,
    val origin: String?,
    val roastLevel: String?,
    val grinder: String?,
    val grindSize: String?,
    val coffeeGrams: Double?,
    val waterGrams: Double?,
    val waterTemp: Int?,
    val targetYield: Int?,
    val isPublic: Boolean = true,
    val steps: List<RecipeStepRequest> = emptyList(),
    val tags: List<String> = emptyList(),
    val beanId: Long? = null,
    val dripperId: Long? = null
)

data class UpdateRecipeRequest(
    val title: String,
    val description: String?,
    val method: String?,
    val coffeeBean: String?,
    val origin: String?,
    val roastLevel: String?,
    val grinder: String?,
    val grindSize: String?,
    val coffeeGrams: Double?,
    val waterGrams: Double?,
    val waterTemp: Int?,
    val targetYield: Int?,
    val isPublic: Boolean = true,
    val steps: List<RecipeStepRequest> = emptyList(),
    val tags: List<String> = emptyList(),
    val beanId: Long? = null,
    val dripperId: Long? = null
)

data class RecipeStepResponse(
    val id: Long,
    val stepOrder: Int,
    val label: String,
    val duration: Int,
    val waterAmount: Int?
)

data class RecipeResponse(
    val id: Long,
    val memberId: Long,
    val title: String,
    val description: String?,
    val method: String?,
    val coffeeBean: String?,
    val origin: String?,
    val roastLevel: String?,
    val grinder: String?,
    val grindSize: String?,
    val coffeeGrams: Double?,
    val waterGrams: Double?,
    val waterTemp: Int?,
    val targetYield: Int?,
    val publishedAt: String?,
    val likeCount: Int,
    val steps: List<RecipeStepResponse>,
    val tags: List<String>,
    val createdAt: String,
    val updatedAt: String,
    val beanId: Long? = null,
    val bean: BeanSummaryResponse? = null,
    val dripperId: Long? = null,
    val dripper: DripperSummaryResponse? = null
)

data class FeedResponse(
    val items: List<RecipeResponse>,
    val nextCursor: Long?
)

data class CommentResponse(
    val id: Long,
    val memberId: Long,
    val content: String,
    val createdAt: String
)

data class CommentRequest(val content: String)
