package com.abrantesv.nutrihub.users

import com.abrantesv.nutrihub.patient.Patient
import com.abrantesv.nutrihub.roles.Role
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.jetbrains.annotations.NotNull

@Entity
@Table(name = "tblUser")
class User(
    @Id @GeneratedValue var id: Long? = null,
    @Column(unique = true, nullable = false) var email: String = "",
    @NotNull var password: String = "",
    @NotNull var name: String = "",

    @ManyToMany @JoinTable(
        name = "UserRole",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    ) @JsonIgnore val roles: MutableSet<Role> = mutableSetOf(),

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "user_id", nullable = false)
    var patients: MutableList<Patient> = mutableListOf()
)