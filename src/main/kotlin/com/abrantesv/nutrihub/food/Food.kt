package com.abrantesv.nutrihub.food

import jakarta.persistence.*
import org.jetbrains.annotations.NotNull

@Entity
@Table(name = "tblFood")
class Food(
    @Id @GeneratedValue var id: Long? = null,
    @NotNull var name: String = "",
    @NotNull var amount: Int = 0,
    @NotNull var unit: String = ""
)