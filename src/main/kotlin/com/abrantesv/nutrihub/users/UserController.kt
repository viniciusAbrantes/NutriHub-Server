package com.abrantesv.nutrihub.users

import com.abrantesv.nutrihub.food.FoodRequest
import com.abrantesv.nutrihub.meal.MealRequest
import com.abrantesv.nutrihub.patient.PatientRequest
import com.abrantesv.nutrihub.plan.PlanRequest
import com.abrantesv.nutrihub.security.SecurityConfig
import com.abrantesv.nutrihub.users.requests.LoginRequest
import com.abrantesv.nutrihub.users.requests.UserRequest
import com.abrantesv.nutrihub.users.responses.LoginResponse
import com.abrantesv.nutrihub.users.responses.UserResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(val userService: UserService) {
    @GetMapping
    fun findAll(
        @RequestParam sortMethod: String? = null, @RequestParam role: String? = null
    ): ResponseEntity<List<UserResponse>> {
        return SortMethod.entries.firstOrNull { it.name == (sortMethod ?: "ASC").uppercase() }
            ?.let { userService.findAll(it, role) }?.map { UserResponse(it) }?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.badRequest().build()
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<UserResponse> {
        return userService.findByIdOrNull(id)?.let { UserResponse(it) }?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @PostMapping()
    fun insert(@RequestBody @Valid user: UserRequest): ResponseEntity<User> {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user.toUser()))
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME_NAME)
    fun deleteById(@PathVariable id: Long): ResponseEntity<Void> {
        return userService.delete(id)?.let { ResponseEntity.ok().build() } ?: ResponseEntity.notFound().build()
    }

    @PutMapping("/{id}/roles/{role}")
    @SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME_NAME)
    fun grant(@PathVariable id: Long, @PathVariable role: String): ResponseEntity<Void> {
        return if (userService.addRole(id, role)) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.noContent().build()
        }
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody login: LoginRequest): ResponseEntity<LoginResponse> {
        return userService.login(login.email!!, login.password!!)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.status(
                HttpStatus.UNAUTHORIZED
            ).build()
    }

    @PostMapping("/{id}/patients")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME_NAME)
    fun addPatient(@PathVariable id: Long, @RequestBody @Valid patient: PatientRequest): ResponseEntity<User> {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addPatient(id, patient))
    }

    @PutMapping("/{id}/patients/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME_NAME)
    fun updatePatient(
        @PathVariable id: Long, @PathVariable patientId: Long, @RequestBody @Valid patient: PatientRequest
    ): ResponseEntity<Void> {
        return if (userService.updatePatient(id, patientId, patient)) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.noContent().build()
        }
    }

    @DeleteMapping("/{id}/patients/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME_NAME)
    fun deletePatientById(@PathVariable id: Long, @PathVariable patientId: Long): ResponseEntity<Void> {
        return userService.deletePatientById(id, patientId)?.let { ResponseEntity.ok().build() }
            ?: ResponseEntity.notFound().build()
    }

    @PostMapping("/{id}/patients/{patientId}/plan")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME_NAME)
    fun addPlan(
        @PathVariable id: Long, @PathVariable patientId: Long, @RequestBody @Valid plan: PlanRequest
    ): ResponseEntity<User> {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addPlan(id, patientId, plan))
    }

    @DeleteMapping("/{id}/patients/{patientId}/plan")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME_NAME)
    fun deletePlan(@PathVariable id: Long, @PathVariable patientId: Long): ResponseEntity<Void> {
        return userService.deletePlan(id, patientId)?.let { ResponseEntity.ok().build() }
            ?: ResponseEntity.notFound().build()
    }

    @PostMapping("/{id}/patients/{patientId}/plan/meals")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME_NAME)
    fun addMeal(
        @PathVariable id: Long, @PathVariable patientId: Long, @RequestBody @Valid meal: MealRequest
    ): ResponseEntity<User> {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addMeal(id, patientId, meal))
    }

    @PutMapping("/{id}/patients/{patientId}/plan/meals/{mealId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME_NAME)
    fun updateMeal(
        @PathVariable id: Long,
        @PathVariable patientId: Long,
        @PathVariable mealId: Long,
        @RequestBody @Valid meal: MealRequest
    ): ResponseEntity<Void> {
        return if (userService.updateMeal(id, patientId, mealId, meal)) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.noContent().build()
        }
    }

    @DeleteMapping("/{id}/patients/{patientId}/plan/meals/{mealId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME_NAME)
    fun deleteMeal(
        @PathVariable id: Long,
        @PathVariable patientId: Long,
        @PathVariable mealId: Long,
    ): ResponseEntity<Void> {
        return userService.deleteMeal(id, patientId, mealId)?.let { ResponseEntity.ok().build() }
            ?: ResponseEntity.notFound().build()
    }

    @PostMapping("/{id}/patients/{patientId}/plan/meals/{mealId}/foods")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME_NAME)
    fun addFood(
        @PathVariable id: Long,
        @PathVariable patientId: Long,
        @PathVariable mealId: Long,
        @RequestBody @Valid food: FoodRequest
    ): ResponseEntity<User> {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addFood(id, patientId, mealId, food))
    }

    @PutMapping("/{id}/patients/{patientId}/plan/meals/{mealId}/foods/{foodId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME_NAME)
    fun updateFood(
        @PathVariable id: Long,
        @PathVariable patientId: Long,
        @PathVariable mealId: Long,
        @PathVariable foodId: Long,
        @RequestBody @Valid food: FoodRequest
    ): ResponseEntity<Void> {
        return if (userService.updateFood(id, patientId, mealId, foodId, food)) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.noContent().build()
        }
    }

    @DeleteMapping("/{id}/patients/{patientId}/plan/meals/{mealId}/foods/{foodId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME_NAME)
    fun deleteFood(
        @PathVariable id: Long,
        @PathVariable patientId: Long,
        @PathVariable mealId: Long,
        @PathVariable foodId: Long,
    ): ResponseEntity<Void> {
        return userService.deleteFood(id, patientId, mealId, foodId)?.let { ResponseEntity.ok().build() }
            ?: ResponseEntity.notFound().build()
    }
}