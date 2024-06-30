package com.example.pets.repository

import com.example.pets.models.Pet
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface PetRepository : MongoRepository<Pet, String> {
    fun findOneById(id: ObjectId): Pet?
}