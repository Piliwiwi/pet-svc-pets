package com.example.pets.models.pet

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId

@Document("animals")
data class Animal(
    @MongoId
    @JsonIgnore
    var id: ObjectId = ObjectId.get(),
    val type: AnimalType = AnimalType.UNSPECIFIED,
    val name: String = "",
    val refImage: String= "",

    /** Relations */
    var breeds: List<Breed> = emptyList()
)

enum class AnimalType {
    UNSPECIFIED, DOG, CAT
}
