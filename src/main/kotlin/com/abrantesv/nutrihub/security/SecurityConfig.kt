package com.abrantesv.nutrihub.security

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher
import org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.handler.HandlerMappingIntrospector

@Configuration
@EnableMethodSecurity
@SecurityScheme(
    name = SecurityConfig.SECURITY_SCHEME_NAME, type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT"
)
class SecurityConfig(val jwtTokenFilter: JwtTokenFilter) {
    @Bean
    fun mvc(introspector: HandlerMappingIntrospector) = MvcRequestMatcher.Builder(introspector)

    @Bean
    fun filterChain(security: HttpSecurity, mvc: MvcRequestMatcher.Builder): DefaultSecurityFilterChain {
        return security.cors(Customizer.withDefaults()).csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(STATELESS) }.exceptionHandling {
                it.authenticationEntryPoint { _, response, exception ->
                    val errorMessage = if (exception.message.isNullOrEmpty()) {
                        UNAUTHORIZED_ERROR_MESSAGE
                    } else {
                        exception.message
                    }
                    response.sendError(SC_UNAUTHORIZED, errorMessage)
                }
            }.headers {
                it.frameOptions { options -> options.disable() }
            }.authorizeHttpRequests { requests ->
                requests.requestMatchers(antMatcher(HttpMethod.GET)).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/users")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/users/login")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.PUT, "/users")).permitAll()
                    .requestMatchers(antMatcher("/h2-console/**")).permitAll().anyRequest().authenticated()
            }.addFilterBefore(jwtTokenFilter, BasicAuthenticationFilter::class.java).build()
    }

    @Bean
    fun corsFilter() = CorsConfiguration().apply {
        addAllowedHeader("*")
        addAllowedOrigin("*")
        addAllowedMethod("*")
    }.let {
        UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", it)
        }
    }.let { CorsFilter(it) }

    companion object {
        private const val UNAUTHORIZED_ERROR_MESSAGE = "Unauthorized"
        const val SECURITY_SCHEME_NAME = "WebToken"
    }
}