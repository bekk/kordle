package no.bekk.kordle.server.service

import no.bekk.kordle.server.repository.UserRepository
import no.bekk.kordle.shared.dto.User
import org.springframework.stereotype.Service

@Service
class UserService(val userRepository: UserRepository) {
    fun getUserByUsername(username: String): User? {
        return userRepository.getUserByUsername(username)
    }

    fun createUser(username: String): User {
        userRepository.createUser(username)
        return userRepository.getUserByUsername(username)
            ?: throw IllegalStateException("User creation failed for username: $username")
    }
}
