package com.abrantesv.nutrihub.plan

import com.abrantesv.nutrihub.meal.Meal
import jakarta.persistence.*
import org.jetbrains.annotations.NotNull

@Entity
@Table(name = "tblPlan")
class Plan(
    @Id @GeneratedValue var id: Long? = null,
    @NotNull var name: String = "",

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "plan_id", nullable = false)
    var meals: MutableList<Meal> = mutableListOf()
)