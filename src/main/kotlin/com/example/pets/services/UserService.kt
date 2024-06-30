package com.example.pets.services

import com.example.pets.dtos.error.UserNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class UserService(@Autowired private val restTemplate: RestTemplate) {
    @Value("\${user.api.url}")
    private lateinit var userApiUrl: String

    fun addPetToUser(userId: String, petId: String): ResponseEntity<Void> {
        val uriVariables = mapOf("userId" to userId, "petId" to petId)
        val responseEntity = restTemplate.postForEntity(
            "${userApiUrl}/add-pet-to-user?userId={userId}&petId={petId}",
            null,
            Void::class.java,
            uriVariables
        )

        if (responseEntity.statusCode == HttpStatus.OK) {
            return responseEntity
        } else {
            throw UserNotFoundException("User not found")
        }
    }
}
