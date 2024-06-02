package com.abrantesv.nutrihub.patient

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

class PatientRequest(
    @field:NotBlank val name: String,
    @field:Email val email: String,
    val age: Int,
    @field:NotBlank val sex: String,
    val height: Float,
    val weight: Float,
) {
    fun toPatient(): Patient {
        return Patient(name = name, email = email, age = age, sex = sex, height = height, weight = weight)
    }
}