package no.bekk.kordle.server.controller

import no.bekk.kordle.server.service.OppgaveService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class OppgaveController(
    private val oppgaveService: OppgaveService,
) {

    @GetMapping("/hello")
    fun helloWorld(): String {
        return "HELLO WORLD FROM OPPGAVE CONTROLLER"
    }
}
