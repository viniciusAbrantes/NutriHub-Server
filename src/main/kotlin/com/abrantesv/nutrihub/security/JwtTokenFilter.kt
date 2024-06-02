package com.abrantesv.nutrihub.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean

@Component
class JwtTokenFilter(private val jwt: Jwt) : GenericFilterBean() {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        jwt.extract(request as HttpServletRequest)?.let { auth ->
            SecurityContextHolder.getContext().authentication = auth
        }
        chain.doFilter(request, response)
    }
}