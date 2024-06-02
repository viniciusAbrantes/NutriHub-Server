package com.abrantesv.nutrihub.food

import jakarta.validation.constraints.NotBlank

data class FoodRequest(
    @field:NotBlank val name: String,
    val amount: Int,
    @field:NotBlank val unit: String
) {
    fun toFood(): Food {
        return Food(name = name, amount = amount, unit = unit)
    }
}