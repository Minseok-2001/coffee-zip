package org.coffeezip.dto

import java.time.LocalDateTime

data class CreateDripperRequest(
    val name: String,
    val brand: String,
    val type: String,
    val material: String,
    val holesCount: Int? = null,
    val extractionSpeed: String? = null,
    val description: String? = null
)

data class UpdateDripperRequest(
    val name: String? = null,
    val brand: String? = null,
    val type: String? = null,
    val material: String? = null,
    val holesCount: Int? = null,
    val extractionSpeed: String? = null,
    val description: String? = null
)

data class UpsertDripperReviewRequest(
    val rating: Int,
    val content: String? = null,
    val extractionRate: Int? = null,
    val cleanability: Int? = null,
    val durability: Int? = null,
    val heatRetention: Int? = null
)

data class DripperSummaryResponse(
    val id: Long,
    val name: String,
    val brand: String,
    val type: String,
    val material: String,
    val extractionSpeed: String?,
    val avgRating: Double?,
    val reviewCount: Int
)

data class DripperDetailResponse(
    val id: Long,
    val name: String,
    val brand: String,
    val type: String,
    val material: String,
    val holesCount: Int?,
    val extractionSpeed: String?,
    val description: String?,
    val avgRating: Double?,
    val reviewCount: Int,
    val recipeCount: Int,
    val avgExtractionRate: Double?,
    val avgCleanability: Double?,
    val avgDurability: Double?,
    val avgHeatRetention: Double?,
    val createdBy: Long,
    val createdAt: LocalDateTime
)

data class DripperReviewResponse(
    val id: Long,
    val dripperId: Long,
    val memberId: Long,
    val rating: Int,
    val content: String?,
    val extractionRate: Int?,
    val cleanability: Int?,
    val durability: Int?,
    val heatRetention: Int?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
