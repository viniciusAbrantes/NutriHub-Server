package com.abrantesv.nutrihub

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NutriHubApplication

fun main(args: Array<String>) {
	runApplication<NutriHubApplication>(*args)
}