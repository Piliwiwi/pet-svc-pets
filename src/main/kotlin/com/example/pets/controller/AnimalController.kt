package com.example.pets.controller

import com.example.pets.dtos.error.Message
import com.example.pets.models.pet.Animal
import com.example.pets.models.pet.AnimalType
import com.example.pets.models.pet.Breed
import com.example.pets.services.AnimalService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
class AnimalController(private val animalService: AnimalService) {

    @GetMapping("/animal/{animalType}")
    fun getAnimal(@PathVariable("animalType") animalType: AnimalType): ResponseEntity<Any> {
        val savedAnimal = animalService.getByType(animalType)
        return if (savedAnimal != null) {
            ResponseEntity.ok(savedAnimal)
        } else {
            ResponseEntity.status(403).body(Message("No existe el animal"))
        }
    }

    @GetMapping("/animals")
    fun getAllAnimals(): ResponseEntity<Any> {
        val animals = animalService.getAllAnimals()
        return if (animals.isEmpty()) {
            ResponseEntity.status(204).body(Message("No existen animales"))
        } else {
            ResponseEntity.ok(animals)
        }
    }

    /** Internal */
    @PostMapping("/internal/add-animal")
    fun addAnimal(@RequestBody body: Animal): ResponseEntity<Animal> {
        val savedAnimal = animalService.save(body)
        return ResponseEntity.ok(savedAnimal)
    }

    @PatchMapping("/internal/update-animal")
    fun updateAnimalByType(@RequestParam("type") type: AnimalType, @RequestBody updatedFields: Map<String, Any>): ResponseEntity<String> {
        val result = animalService.updateAnimalByType(type, updatedFields)

        return if (result) {
            ResponseEntity.ok("Animal actualizado con éxito.")
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró un animal con el tipo proporcionado.")
        }
    }

    @PostMapping("/internal/add-breed")
    fun addBreed(@RequestParam("animal") type: AnimalType, @RequestBody body: Breed): ResponseEntity<String> {
        val result = animalService.addBreedToAnimal(type, body)

        return if (result) {
            ResponseEntity.ok("Raza agregada con exito a ${type.name}.")
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró el tipo de animal proporcionado.")
        }
    }

    @PatchMapping("/internal/update-breed")
    fun updateBreed(@RequestParam("breedCode") breedCode: String, @RequestParam("animal") type: AnimalType, @RequestBody updatedFields: Map<String, Any>): ResponseEntity<String> {
        val result = animalService.updateBreedFromAnimal(type, breedCode, updatedFields)

        return if (result) {
            ResponseEntity.ok("Raza modificada con exito en ${type.name}.")
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró el tipo de animal proporcionado o la raza.")
        }
    }
}