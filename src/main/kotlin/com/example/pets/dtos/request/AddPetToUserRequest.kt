package com.example.pets.dtos.request

data class AddPetToUserRequest(
    val userId: String,
    val petId: String
)
