package com.abrantesv.nutrihub.meal

import jakarta.validation.constraints.NotBlank

class MealRequest(@field:NotBlank val name: String) {
    fun toMeal(): Meal {
        return Meal(name = name)
    }
}