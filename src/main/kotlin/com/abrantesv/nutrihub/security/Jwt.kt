package com.abrantesv.nutrihub.security

import com.abrantesv.nutrihub.users.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.jackson.io.JacksonDeserializer
import io.jsonwebtoken.jackson.io.JacksonSerializer
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

@Component
class Jwt {
    fun createToken(user: User): String {
        return UserToken(user).let {
            Jwts.builder().signWith(Keys.hmacShaKeyFor(SIGNATURE.toByteArray())).json(JacksonSerializer())
                .issuedAt(utcNow().toDate()).expiration(
                    utcNow().plusHours(EXPIRATION_HOURS).toDate()
                ).issuer(ISSUER).subject(user.id.toString()).claim(USER_FIELD, it).compact()
        }
    }

    fun extract(request: HttpServletRequest): Authentication? {
        try {
            val header = request.getHeader(AUTHORIZATION)
            if (header == null || !header.startsWith("Bearer")) return null

            val token = header.replace("Bearer", "").trim()

            val claims = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(SIGNATURE.toByteArray()))
                .json(JacksonDeserializer(mapOf(USER_FIELD to UserToken::class.java))).build()
                .parseSignedClaims(token).payload

            if (claims.issuer != ISSUER) {
                logger?.debug("Received request has unexpected issuer: {}", claims.issuer)
                return null
            }

            return claims.get("user", UserToken::class.java).toAuthentication()
        } catch (e: Throwable) {
            logger?.debug("Token rejected", e)
            return null
        }
    }

    companion object {
        private val logger: Logger? = LoggerFactory.getLogger(Jwt::class.java)
        private const val SIGNATURE = "aab15707b7d91c392facd63ff659c920d8117db2"
        private const val EXPIRATION_HOURS = 2L
        private const val ISSUER = "NutriHub Server"
        private const val USER_FIELD = "user"

        private fun utcNow() = ZonedDateTime.now(ZoneOffset.UTC)
    }
}

private fun ZonedDateTime.toDate(): Date = Date.from(this.toInstant())

private fun UserToken.toAuthentication(): Authentication {
    val authorities = roles.map { SimpleGrantedAuthority("ROLE_$it") }
    return UsernamePasswordAuthenticationToken.authenticated(this, id, authorities)
}