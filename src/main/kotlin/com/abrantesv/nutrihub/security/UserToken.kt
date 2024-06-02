package com.abrantesv.nutrihub.security

import com.abrantesv.nutrihub.users.User

data class UserToken(val id: Long, val name: String, val roles: Set<String>) {
    constructor() : this(0, "", setOf())

    constructor(user: User) : this(
        id = user.id!!, name = user.name, roles = user.roles.map { it.name }.toSortedSet()
    )
}