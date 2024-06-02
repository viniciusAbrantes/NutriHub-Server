package com.abrantesv.nutrihub.users

import com.abrantesv.nutrihub.food.FoodRequest
import com.abrantesv.nutrihub.meal.MealRequest
import com.abrantesv.nutrihub.patient.PatientRequest
import com.abrantesv.nutrihub.plan.PlanRequest
import com.abrantesv.nutrihub.roles.Role
import com.abrantesv.nutrihub.roles.RoleRepository
import com.abrantesv.nutrihub.security.Jwt
import com.abrantesv.nutrihub.users.responses.LoginResponse
import com.abrantesv.nutrihub.users.responses.UserResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository,
    val roleRepository: RoleRepository,
    val jwt: Jwt,
) {
    fun findAll(dir: SortMethod, role: String?) = role?.let { r ->
        when (dir) {
            SortMethod.ASC -> userRepository.findByRole(r.uppercase()).sortedBy { it.name }
            SortMethod.DESC -> userRepository.findByRole(r.uppercase()).sortedByDescending { it.name }
        }
    } ?: when (dir) {
        SortMethod.ASC -> userRepository.findAll(Sort.by("name").ascending())
        SortMethod.DESC -> userRepository.findAll(Sort.by("name").descending())
    }

    fun findByIdOrNull(userId: Long): User? {
        return userRepository.findByIdOrNull(userId)
    }

    fun save(user: User): User {
        return userRepository.save(user)
    }

    fun delete(userId: Long): User? {
        roleRepository.findByName(Role.ADMIN_ROLE)?.let { adminRole ->
            val userToDelete = userRepository.findByIdOrNull(userId) ?: return null
            if (userToDelete.roles.any { it.id == adminRole.id }) {
                if (userRepository.findByRole(Role.ADMIN_ROLE).size == 1) return null
            }
        }

        return userRepository.findByIdOrNull(userId).also { userRepository.deleteById(userId) }
    }

    fun addRole(userId: Long, roleName: String): Boolean {
        val user = userRepository.findByIdOrNull(userId) ?: run {
            logger?.warn("User {} not found when trying to add role!", userId)
            throw IllegalArgumentException("User $userId not found!")
        }
        if (user.roles.any { it.name == roleName }) return false
        val role = roleRepository.findByName(roleName) ?: run {
            logger?.warn("Invalid role {}!", roleName)
            throw IllegalArgumentException("Invalid role $roleName!")
        }

        user.roles.add(role)
        userRepository.save(user)
        return true
    }

    fun login(email: String, password: String): LoginResponse? {
        val user = userRepository.findByEmail(email).firstOrNull()

        if (user == null) {
            logger?.warn("User {} not found!", email)
            return null
        }

        if (password != user.password) {
            logger?.warn("Invalid password: id={}, name={}", user.id, user.name)
            return null
        }

        logger?.info("User logged in: id={}, name={}", user.id, user.name)

        return LoginResponse(jwt.createToken(user), UserResponse(user))
    }

    fun addPatient(userId: Long, patient: PatientRequest): User {
        userRepository.findByIdOrNull(userId)?.let { user ->
            user.patients.add(patient.toPatient())
            return userRepository.save(user)
        }
        logger?.warn("User {} not found when trying to add patient!", userId)
        throw IllegalArgumentException("User $userId not found!")
    }

    fun updatePatient(userId: Long, patientId: Long, patientRequest: PatientRequest): Boolean {
        val user = userRepository.findByIdOrNull(userId) ?: run {
            logger?.warn("User {} not found when trying to update patient!", userId)
            throw IllegalArgumentException("User $userId not found!")
        }
        user.patients.map { patient ->
            if (patient.id == patientId) {
                patient.apply {
                    name = patientRequest.name
                    email = patientRequest.email
                    age = patientRequest.age
                    sex = patientRequest.sex
                    height = patientRequest.height
                    weight = patientRequest.weight
                    userRepository.save(user)
                }
                return true
            }
        }
        return false
    }

    fun deletePatientById(userId: Long, patientId: Long): User? {
        val user = userRepository.findByIdOrNull(userId) ?: run {
            logger?.warn("User {} not found when trying to delete patient!", userId)
            throw IllegalArgumentException("User $userId not found!")
        }
        user.patients.map { patient ->
            if (patient.id == patientId) {
                user.patients.remove(patient)
                return userRepository.save(user)
            }
        }
        return null
    }

    fun addPlan(userId: Long, patientId: Long, plan: PlanRequest): User? {
        userRepository.findByIdOrNull(userId)?.let { user ->
            user.patients.map { patient ->
                if (patient.id == patientId) {
                    patient.plan = plan.toPlan()
                    return userRepository.save(user)
                }
            }
        }
        logger?.warn("User {} not found when trying to add plan!", userId)
        throw throw IllegalArgumentException("User $userId not found!")
    }

    fun deletePlan(userId: Long, patientId: Long): User? {
        val user = userRepository.findByIdOrNull(userId) ?: run {
            logger?.warn("User {} not found when trying to delete plan!", userId)
            throw IllegalArgumentException("User $userId not found!")
        }
        user.patients.map { patient ->
            if (patient.id == patientId) {
                patient.plan = null
                return userRepository.save(user)
            }
        }
        return null
    }

    fun addMeal(userId: Long, patientId: Long, meal: MealRequest): User? {
        userRepository.findByIdOrNull(userId)?.let { user ->
            user.patients.map { patient ->
                if (patient.id == patientId) {
                    patient.plan?.meals?.add(meal.toMeal())
                    return userRepository.save(user)
                }
            }
        }
        logger?.warn("User {} not found when trying to add meal!", userId)
        throw IllegalArgumentException("User $userId not found!")
    }

    fun updateMeal(userId: Long, patientId: Long, mealId: Long, mealRequest: MealRequest): Boolean {
        val user = userRepository.findByIdOrNull(userId) ?: run {
            logger?.warn("User {} not found when trying to update meal!", userId)
            throw IllegalArgumentException("User $userId not found!")
        }
        user.patients.map { patient ->
            if (patient.id == patientId) {
                patient.plan?.meals?.map { meal ->
                    if (meal.id == mealId) {
                        meal.name = mealRequest.name
                        userRepository.save(user)
                        return true
                    }
                }
            }
        }
        return false
    }

    fun deleteMeal(userId: Long, patientId: Long, mealId: Long): User? {
        val user = userRepository.findByIdOrNull(userId) ?: run {
            logger?.warn("User {} not found when trying to delete meal!", userId)
            throw IllegalArgumentException("User $userId not found!")
        }
        user.patients.map { patient ->
            if (patient.id == patientId) {
                patient.plan?.meals?.map { meal ->
                    if (meal.id == mealId) {
                        patient.plan?.meals?.remove(meal)
                        return userRepository.save(user)
                    }
                }
            }
        }
        return null
    }

    fun addFood(userId: Long, patientId: Long, mealId: Long, food: FoodRequest): User? {
        userRepository.findByIdOrNull(userId)?.let { user ->
            user.patients.map { patient ->
                if (patient.id == patientId) {
                    patient.plan?.meals?.map { meal ->
                        if (meal.id == mealId) {
                            meal.foods.add(food.toFood())
                            return userRepository.save(user)
                        }
                    }
                }
            }
        }
        logger?.warn("User {} not found when trying to add food!", userId)
        throw IllegalArgumentException("User $userId not found!")
    }

    fun updateFood(userId: Long, patientId: Long, mealId: Long, foodId: Long, foodRequest: FoodRequest): Boolean {
        val user = userRepository.findByIdOrNull(userId) ?: run {
            logger?.warn("User {} not found when trying to update food!", userId)
            throw IllegalArgumentException("User $userId not found!")
        }
        user.patients.map { patient ->
            if (patient.id == patientId) {
                patient.plan?.meals?.map { meal ->
                    if (meal.id == mealId) {
                        meal.foods.map { food ->
                            if (food.id == foodId) {
                                food.name = foodRequest.name
                                userRepository.save(user)
                                return true
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    fun deleteFood(userId: Long, patientId: Long, mealId: Long, foodId: Long): User? {
        val user = userRepository.findByIdOrNull(userId) ?: run {
            logger?.warn("User {} not found when trying to delete food!", userId)
            throw IllegalArgumentException("User $userId not found!")
        }
        user.patients.map { patient ->
            if (patient.id == patientId) {
                patient.plan?.meals?.map { meal ->
                    if (meal.id == mealId) {
                        meal.foods.map { food ->
                            if (food.id == foodId) {
                                meal.foods.remove(food)
                                return userRepository.save(user)
                            }
                        }
                    }
                }
            }
        }
        return null
    }


    companion object {
        private val logger: Logger? = LoggerFactory.getLogger(UserService::class.java)
    }
}