package com.abrantesv.nutrihub.users.responses

data class LoginResponse(val token: String, val user: UserResponse)