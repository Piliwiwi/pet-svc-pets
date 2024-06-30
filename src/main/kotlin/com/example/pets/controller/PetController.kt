package com.example.pets.controller

import com.example.pets.dtos.request.PetRegister
import com.example.pets.models.Pet
import com.example.pets.services.AnimalService
import com.example.pets.services.PetService
import com.example.pets.services.UserService
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/pets")
class PetController(
    private val petService: PetService,
    private val animalService: AnimalService,
    private val userService: UserService
) {
    @PostMapping("/register")
    fun addPet(@RequestBody body: PetRegister): ResponseEntity<Pet> {
        /**  Set Owner */
        val newOwnerList = body.pet.ownerIds.toMutableList()
        newOwnerList.add(body.userId)
        body.pet.ownerIds = newOwnerList

        val animalName = animalService.getByType(body.pet.animalType)
        body.pet.animalName = animalName?.name

        /**  Save Pet */
        val savedPet = petService.save(body.pet)

        /**  Add Pet Reference To User */
        userService.addPetToUser(body.userId, savedPet.id.toHexString())

        return ResponseEntity.ok(savedPet)
    }

    @GetMapping("/{petId}")
    fun getPetById(@PathVariable("petId") petId: String): ResponseEntity<Pet> {
        val objectIdPetId = try {
            ObjectId(petId)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.notFound().build()
        }

        val pet = petService.getByMongoId(objectIdPetId)
        return if (pet != null) {
            ResponseEntity.ok(pet)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/list")
    fun getPetsByIds(@RequestBody petIds: List<String>): ResponseEntity<Map<String, Pet>> {
        val pets = petService.getPetsByMongoIds(petIds)
        return ResponseEntity.ok(pets)
    }

    @PostMapping("/add-owner-to-pet")
    fun addOwnerToPet(@RequestParam("userId") userId: String, @RequestParam("petId") petId: String): ResponseEntity<Pet> {
        return try {
            val pet = petService.addOwnerToPet(userId, petId)
            ResponseEntity.ok(pet)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    @PatchMapping("/update-pet")
    fun updatePet(@RequestParam("id") id: String, @RequestBody updatedFields: Map<String, Any>): ResponseEntity<String> {
        val result = petService.updateAnimalById(id, updatedFields)

        return if (result) {
            ResponseEntity.ok("Mascota actualizada con éxito.")
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró ninguna mascota con el id proporcionado.")
        }
    }

    @DeleteMapping("/{pet-id}")
    fun deletePet(@PathVariable("pet-id") petId: String): ResponseEntity<String> {
        return try {
            val deletedCount = petService.deletePetById(petId)
            ResponseEntity.ok("Mascota eliminada exitosamente")
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ocurrió un error al eliminar la mascota: ${e.message}")
        }
    }
}
