package com.example.pets.dtos.request

import com.example.pets.models.Pet
import org.springframework.web.multipart.MultipartFile

data class PetRegister(
    val userId: String,
    val pet: Pet,
    val petProfilePhoto: MultipartFile? = null
)
