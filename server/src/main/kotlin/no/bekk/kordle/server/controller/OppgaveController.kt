package no.bekk.kordle.server.controller

import no.bekk.kordle.server.exceptions.GjettetErIkkeIOrdlistaException
import no.bekk.kordle.server.exceptions.GjettetHarUgyldigLengdeException
import no.bekk.kordle.server.service.OppgaveService
import no.bekk.kordle.server.service.OrdValidatorService
import no.bekk.kordle.shared.dto.GjettOrdRequest
import no.bekk.kordle.shared.dto.GjettResponse
import no.bekk.kordle.shared.dto.OppgaveResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OppgaveController(
    private val oppgaveService: OppgaveService,
    private val ordValidatorService: OrdValidatorService
) {
    @GetMapping("/health")
    fun healthCheck(): ResponseEntity<String?> {
        return ResponseEntity.ok().body("Kordle server is running")
    }

    @GetMapping("/hentTilfeldigOppgave")
    fun hentTilfeldigOppgave(): OppgaveResponse {
        return oppgaveService.hentTilfeldigOppgave().tilOppgaveResponse()
    }

    @PostMapping("/gjettOrd")
    fun gjettOrd(@RequestBody gjettOrdRequest: GjettOrdRequest): ResponseEntity<*> {
        try {
            val bokstavTreff = oppgaveService.gjettOrd(
                oppgaveId = gjettOrdRequest.oppgaveId,
                ordGjettet = gjettOrdRequest.ordGjett
            )
            val gjettResponse = GjettResponse(
                oppgaveId = gjettOrdRequest.oppgaveId,
                alleBokstavtreff = bokstavTreff.map { it.tilBokstavTreffDTO() }
            )
            return ResponseEntity.ok().body(gjettResponse)

        } catch (exception: RuntimeException) {
            val statusKodeSomSkalReturneres = when (exception) {
                is GjettetErIkkeIOrdlistaException -> HttpStatus.BAD_REQUEST
                is GjettetHarUgyldigLengdeException -> HttpStatus.BAD_REQUEST
                else -> HttpStatus.INTERNAL_SERVER_ERROR
            }
            return ResponseEntity
                .status(statusKodeSomSkalReturneres)
                .body(exception.message)
        }
    }
}



