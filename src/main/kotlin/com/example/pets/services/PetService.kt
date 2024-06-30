package com.example.pets.services

import com.example.pets.dtos.error.PetNotFoundException
import com.example.pets.models.Pet
import com.example.pets.repository.PetRepository
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service

@Service
class PetService(
    private val repository: PetRepository,
    private val mongoTemplate: MongoTemplate
) {
    fun save(pet: Pet): Pet = repository.insert(pet)

    fun getByMongoId(mongoId: ObjectId): Pet? = repository.findOneById(mongoId)

    fun getPetsByMongoIds(objectIds: List<String>): Map<String, Pet> {
        val pets: List<Pet> = repository.findAllById(objectIds)
        return pets.associateBy { it.id.toString() }
    }

    fun addOwnerToPet(userId: String, petId: String): Pet? {
        val pet = repository.findOneById(ObjectId(petId))

        val newOwners = pet?.ownerIds?.toMutableList()
        newOwners?.add(userId)

        newOwners?.let { pet.ownerIds = it }
        if (pet == null) throw PetNotFoundException("pet not found")
        else return repository.save(pet)
    }

    fun updateAnimalById(petId: String, updatedFields: Map<String, Any>): Boolean {
        val query = Query.query(Criteria.where("id").`is`(ObjectId(petId)))

        val update = Update()
        updatedFields.forEach { (key, value) -> update.set(key, value) }

        val options = FindAndModifyOptions()
        options.returnNew(true)

        val updatedAnimal = mongoTemplate.findAndModify(
            query, update, options, Pet::class.java
        )

        return updatedAnimal != null
    }

    fun deletePetById(id: String) {
        repository.deleteById(id)
    }
}