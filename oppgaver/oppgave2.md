# Oppgave 2: Gjetting og buisness logic

Så langt i oppgavene har vi laget en serverapplikasjon som kan hente ut en tilfeldig oppgave.
Dessverre kan vi ende ikke utføre gjett på oppgaven, da vi ikke har laget noen logikk for å håndtere gjetting 😱
Dette er det vi skal gjøre i denne oppgaven!

Som nevnt tidligere, er det viktig at vi holder foretningslogikken samlet på samme sted. I spring
samles slik foretningslogikk i `services`, det vil si klasser som er annotert med `@Service`.
I denne opppgaven skal vi utvide `OppgaveService.kt` for å håndtere gjetting av oppgaver.

I fila [oppgave.kt](../shared/src/main/kotlin/no/bekk/kordle/shared/dto/oppgave.kt) finner du to klasser:

```kotlin
@Serializable
data class GjettOrdRequest(
    val oppgaveId: Int,
    val ordGjett: String
)


@Serializable
data class GjettResponse(
    val oppgaveId: Int,
    val alleBokstavtreff: List<BokstavTreff>
)
```

Oppgave 2.1: 
