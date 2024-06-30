package com.example.pets.models

import com.example.pets.models.Gender.UNKNOWN
import com.example.pets.models.pet.AnimalType
import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDate
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId

@Document("pets")
class Pet {
    @MongoId
    @JsonIgnore
    var id: ObjectId = ObjectId.get()
    var name: String = ""
    var breedCode: String? = null
    var genre: Gender = UNKNOWN
    var animalName: String? = null
    var description: String? = null
    var birthDate: LocalDate? = null
    var nickName: String? = null
    var profilePhoto: String? = null
    var ownerIds: List<String> = emptyList()

    /** Scheme Relations */
    var animalType: AnimalType = AnimalType.UNSPECIFIED
}

enum class Gender {
    UNKNOWN, F, M
}
