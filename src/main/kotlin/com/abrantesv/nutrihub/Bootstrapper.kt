package com.abrantesv.nutrihub

import com.abrantesv.nutrihub.roles.Role
import com.abrantesv.nutrihub.roles.RoleRepository
import com.abrantesv.nutrihub.users.User
import com.abrantesv.nutrihub.users.UserRepository
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
class Bootstrapper(
    val userRepository: UserRepository,
    val roleRepository: RoleRepository
): ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val adminRole = roleRepository.findByName(Role.ADMIN_ROLE)
            ?: roleRepository
                .save(Role(name = Role.ADMIN_ROLE, description = "System administrator"))
                .also { roleRepository.save(Role(name = Role.PATIENT_ROLE, description = "Patient user")) }
                .also { roleRepository.save(Role(name = Role.USER_ROLE, description = "Dietitian user")) }

        if (userRepository.findByRole(adminRole.name).isEmpty()) {
            val admin = User(
                name = "NutriHub Administrator",
                email = "admin@nutrihub.com",
                password = "nutrihub_admin"
            )
            admin.roles.add(adminRole)
            userRepository.save(admin)
        }
    }
}