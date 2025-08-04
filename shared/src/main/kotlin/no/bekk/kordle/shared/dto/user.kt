package no.bekk.kordle.shared.dto

import kotlinx.serialization.Serializable


@Serializable
data class User(
    val id: Int,
    val username: String,
)

@Serializable
data class CreateUserRequest(
    val username: String,
)
