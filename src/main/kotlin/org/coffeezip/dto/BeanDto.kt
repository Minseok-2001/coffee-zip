package org.coffeezip.dto

import java.time.LocalDateTime

// ── Request ──────────────────────────────────────────────────────────

data class CreateBeanRequest(
    val name: String,
    val roastery: String,
    val origin: String,
    val region: String? = null,
    val farm: String? = null,
    val variety: String? = null,
    val processing: String? = null,
    val roastLevel: String,
    val altitude: Int? = null,
    val harvestYear: Int? = null,
    val description: String? = null,
    val flavorNotes: List<String> = emptyList()
)

data class UpdateBeanRequest(
    val name: String? = null,
    val roastery: String? = null,
    val origin: String? = null,
    val region: String? = null,
    val farm: String? = null,
    val variety: String? = null,
    val processing: String? = null,
    val roastLevel: String? = null,
    val altitude: Int? = null,
    val harvestYear: Int? = null,
    val description: String? = null,
    val flavorNotes: List<String>? = null
)

data class UpsertBeanReviewRequest(
    val rating: Int,             // 1–5
    val content: String? = null,
    val acidity: Int? = null,    // 1–5
    val sweetness: Int? = null,
    val body: Int? = null,
    val aroma: Int? = null
)

// ── Response ─────────────────────────────────────────────────────────

data class BeanSummaryResponse(
    val id: Long,
    val name: String,
    val roastery: String,
    val origin: String,
    val roastLevel: String,
    val flavorNotes: List<String>,
    val avgRating: Double?,
    val reviewCount: Int
)

data class BeanDetailResponse(
    val id: Long,
    val name: String,
    val roastery: String,
    val origin: String,
    val region: String?,
    val farm: String?,
    val variety: String?,
    val processing: String?,
    val roastLevel: String,
    val altitude: Int?,
    val harvestYear: Int?,
    val description: String?,
    val flavorNotes: List<String>,
    val avgRating: Double?,
    val reviewCount: Int,
    val recipeCount: Int,
    val avgAcidity: Double?,
    val avgSweetness: Double?,
    val avgBody: Double?,
    val avgAroma: Double?,
    val createdBy: Long,
    val createdAt: LocalDateTime
)

data class BeanReviewResponse(
    val id: Long,
    val beanId: Long,
    val memberId: Long,
    val rating: Int,
    val content: String?,
    val acidity: Int?,
    val sweetness: Int?,
    val body: Int?,
    val aroma: Int?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
