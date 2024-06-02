package com.abrantesv.nutrihub.plan

import jakarta.validation.constraints.NotBlank

class PlanRequest(@field:NotBlank val name: String) {
    fun toPlan(): Plan {
        return Plan(name = name)
    }
}