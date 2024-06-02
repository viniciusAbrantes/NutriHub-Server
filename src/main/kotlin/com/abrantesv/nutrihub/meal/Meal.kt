package com.abrantesv.nutrihub.meal

import com.abrantesv.nutrihub.food.Food
import jakarta.persistence.*
import org.jetbrains.annotations.NotNull

@Entity
@Table(name = "tblMeal")
class Meal(
    @Id @GeneratedValue var id: Long? = null,
    @NotNull var name: String = "",

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "meal_id", nullable = false)
    var foods: MutableList<Food> = mutableListOf()
)