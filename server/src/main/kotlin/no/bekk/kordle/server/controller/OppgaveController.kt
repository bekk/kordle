package no.bekk.kordle.server.controller

import no.bekk.kordle.server.dto.LeggTilOrdRequest
import no.bekk.kordle.server.dto.Oppgave
import no.bekk.kordle.server.exceptions.OrdetEksistererAlleredeIDatabasenException
import no.bekk.kordle.server.exceptions.OrdetHarUgyldigLengdeException
import no.bekk.kordle.server.service.OppgaveService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OppgaveController(
    private val oppgaveService: OppgaveService,
) {

    @GetMapping("/hello")
    fun helloWorld(): String {
        return "HELLO WORLD FROM OPPGAVE CONTROLLER"
    }

    @GetMapping("/hentTilfeldigOppgave")
    fun hentTilfeldigOppgave(): Oppgave {
        return oppgaveService.hentTilfeldigOppgave()
    }

    @PostMapping("/leggTilOrd")
    fun leggTilOrd(@RequestBody leggTilOrdRequest: LeggTilOrdRequest): ResponseEntity<*> {
        try {
            val ordSomSkalLeggesTil = leggTilOrdRequest.ord
            val ordSomBleLagtTil = oppgaveService.leggTilOrd(ordSomSkalLeggesTil)
            return ResponseEntity.ok().body(ordSomBleLagtTil)

        } catch (exception: RuntimeException) {
            val statusKodeSomSkalReturneres = when (exception) {
                is OrdetEksistererAlleredeIDatabasenException -> HttpStatus.CONFLICT
                is OrdetHarUgyldigLengdeException -> HttpStatus.BAD_REQUEST
                else -> HttpStatus.INTERNAL_SERVER_ERROR
            }
            return ResponseEntity
                .status(statusKodeSomSkalReturneres)
                .body(exception.message)
        }
    }
}



