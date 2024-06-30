package com.example.pets.services

import com.example.pets.models.pet.Animal
import com.example.pets.models.pet.AnimalType
import com.example.pets.models.pet.Breed
import com.example.pets.repository.AnimalRepository
import java.util.Optional
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service

@Service
class AnimalService(private val repository: AnimalRepository, private val mongoTemplate: MongoTemplate) {
    fun getByMongoId(mongoId: ObjectId): Animal? = repository.findOneById(mongoId)
    fun getById(id: String): Optional<Animal> = repository.findById(id)
    fun getByType(type: AnimalType): Animal? {
        val animal = repository.findAnimalByType(type)
        return animal
    }
    fun getAllAnimals(): List<Animal> {
        return repository.findAll()
    }
    fun save(animal: Animal): Animal = repository.insert(animal)

    fun updateAnimalByType(animalType: AnimalType, updatedFields: Map<String, Any>): Boolean {
        val query = Query.query(Criteria.where("type").`is`(animalType.name))

        val update = Update()
        updatedFields.forEach { (key, value) -> update.set(key, value) }

        val options = FindAndModifyOptions()
        options.returnNew(true)

        val updatedAnimal = mongoTemplate.findAndModify(
            query, update, options, Animal::class.java
        )

        return updatedAnimal != null
    }

    fun addBreedToAnimal(animalType: AnimalType, newBreed: Breed): Boolean {
        val animal = repository.findAnimalByType(animalType)
        val codes = animal?.breeds?.map { it.code }
        if (animal != null && codes?.contains(newBreed.code) != true) {
            val updatedBreeds = animal.breeds.toMutableList()
            updatedBreeds.add(newBreed)
            animal.breeds = updatedBreeds

            repository.save(animal)
            return true
        }
        return false
    }

    fun updateBreedFromAnimal(animalType: AnimalType, breedCode: String, updatedFields: Map<String, Any>): Boolean {
        val animal = repository.findAnimalByType(animalType)

        if (animal != null) {
            val breedToUpdate = animal.breeds.find { it.code == breedCode }

            if (breedToUpdate != null) {
                val breedClass = Breed::class.java

                updatedFields.forEach { (key, value) ->
                    try {
                        val field = breedClass.getDeclaredField(key)
                        field.isAccessible = true
                        field.set(breedToUpdate, value)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                repository.save(animal)

                return true
            }
        }

        return false
    }
}