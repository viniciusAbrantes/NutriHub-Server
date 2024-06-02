package com.abrantesv.nutrihub.roles

import jakarta.persistence.*

@Entity
@Table(name = "tblRole")
class Role(
    @Id @GeneratedValue var id: Long? = null,
    @Column(unique = true, nullable = false) var name: String,
    var description: String = "",
) {
    companion object {
        const val ADMIN_ROLE = "ADMIN"
        const val PATIENT_ROLE = "PATIENT"
        const val USER_ROLE = "USER"
    }
}