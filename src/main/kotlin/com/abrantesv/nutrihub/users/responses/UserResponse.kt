package com.abrantesv.nutrihub.users.responses

import com.abrantesv.nutrihub.users.User

data class UserResponse(val id: Long, val name: String, val email: String) {
    constructor(user: User) : this(id = user.id!!, name = user.name, email = user.email)
}