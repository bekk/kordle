package no.bekk.kordle.server.controller

import no.bekk.kordle.server.service.UserService
import no.bekk.kordle.shared.dto.CreateUserRequest
import no.bekk.kordle.shared.dto.User
import org.springframework.web.bind.annotation.*

@RestController
class UserController(val userService: UserService) {
    @GetMapping("/users")
    fun getOrCreateUser(@RequestParam("username") username: String): User? {
        return userService.getUserByUsername(username)
    }

    @PostMapping("/users")
    fun createUser(@RequestBody body: CreateUserRequest): User {
        return userService.createUser(body.username)
    }
}
