package com.abrantesv.nutrihub.plan

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlanRepository : JpaRepository<Plan, Long>