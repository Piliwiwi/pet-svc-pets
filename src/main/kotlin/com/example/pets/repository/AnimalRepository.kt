package com.example.pets.repository

import com.example.pets.models.pet.Animal
import com.example.pets.models.pet.AnimalType
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface AnimalRepository : MongoRepository<Animal, String> {
    fun findOneById(id: ObjectId): Animal?
    @Query("{'type': ?0}")
    fun findAnimalByType(type: AnimalType): Animal?
}