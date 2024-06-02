package com.abrantesv.nutrihub.patient

import com.abrantesv.nutrihub.plan.Plan
import jakarta.persistence.*
import org.jetbrains.annotations.NotNull

@Entity
@Table(name = "tblPatient")
class Patient(
    @Id @GeneratedValue var id: Long? = null,
    @NotNull var name: String = "",
    @NotNull var email: String = "",
    @NotNull var age: Int = 0,
    @NotNull var sex: String = "",
    @NotNull var height: Float = 0f,
    @NotNull var weight: Float = 0f,

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "plan_id", nullable = true)
    var plan: Plan? = null
)