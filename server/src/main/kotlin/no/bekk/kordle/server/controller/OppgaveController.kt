package no.bekk.kordle.server.controller

import no.bekk.kordle.server.dto.LeggTilOrdRequest
import no.bekk.kordle.server.dto.Ord
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

    @GetMapping("/ord/hentTilfeldigOrd")
    fun hentAlleOrd(): Ord {
        return oppgaveService.hentTilfeldigOrd()
    }

    @PostMapping("/ord/leggTilOrd")
    fun leggTilOrd(@RequestBody leggTilOrdRequest: LeggTilOrdRequest): ResponseEntity<*> {
        try {
            val tekstFraRequest = leggTilOrdRequest.tekstSomSkalLeggesTil
            val ordSomBleLagtTil = oppgaveService.leggTilOrd(tekstFraRequest)
            return ResponseEntity.ok().body<Ord>(ordSomBleLagtTil)

        } catch (exception: Exception) {
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



