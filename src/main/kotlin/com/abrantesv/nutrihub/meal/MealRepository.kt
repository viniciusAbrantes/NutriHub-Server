package com.abrantesv.nutrihub.meal

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MealRepository : JpaRepository<Meal, Long>