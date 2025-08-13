package no.bekk.kordle.server.utils

import no.bekk.kordle.server.domain.BokstavTreff


/**
 * Funksjon for å finne hvilke bokstaver som er gjettet som eksisterer i ordet og er på riktig plass
 * @param ordIOppgave Ordet som skal gjettes
 * @param ordGjettet Ordet som er gjettet av brukeren
 * @return En liste av BokstavTreff som representerer hvilke bokstaver som er gjettet riktig og er på riktig plass i ordet.
 */
fun finnEksakteBokstavTreff(
    ordIOppgave: String,
    ordGjettet: String,
): List<BokstavTreff> {
    val gjettetOrdIndex: Map<Int, Char> = ordGjettet
        .lowercase()
        .mapIndexed { index, bokstav -> index to bokstav }
        .toMap()

    val eksakteTreffForBokstav = gjettetOrdIndex
        .filter { (index, bokstav) ->
            bokstav == ordIOppgave[index]
        }.map { (index, bokstav) ->
            BokstavTreff(
                plassISekvensen = index,
                bokstavGjettet = bokstav,
                erBokstavenIOrdet = true,
                erBokstavenPaaRettsted = true
            )
        }
    return eksakteTreffForBokstav
}

/**
 * Funksjon for å finne hvilke bokstaver som er gjettet som eksisterer i ordet men er på feil plass i ordet
 * @param ordIOppgave Ordet som skal gjettes
 * @param ordGjettet Ordet som er gjettet av brukeren
 * @return En liste av BokstavTreff som representerer hvilke bokstaver som er gjettet riktig men er på feil plass i ordet.
 */
fun finnDelvisBokstavTreff(
    ordIOppgave: String,
    ordGjettet: String,
): List<BokstavTreff> {
    val gjettetOrdIndex: Map<Int, Char> = ordGjettet
        .lowercase()
        .mapIndexed { index, bokstav -> index to bokstav }
        .toMap()

    val delvisTreffForBokstav = gjettetOrdIndex
        .filter { (index, bokstav) ->
            bokstav != ordIOppgave[index] && ordIOppgave.contains(bokstav)
        }.map { (index, bokstav) ->
            BokstavTreff(
                plassISekvensen = index,
                bokstavGjettet = bokstav,
                erBokstavenIOrdet = true,
                erBokstavenPaaRettsted = false
            )
        }
    return delvisTreffForBokstav
}

fun sjekkBokstavTreff(
    ordIOppgave: String,
    ordGjettet: String
): List<BokstavTreff> {

    val eksakteTreff = finnEksakteBokstavTreff(
        ordIOppgave = ordIOppgave,
        ordGjettet = ordGjettet
    )

    val delvisTreff = finnDelvisBokstavTreff(
        ordIOppgave = ordIOppgave,
        ordGjettet = ordGjettet
    )

    val alleTreff = slaaSammenEksaktOgDelvisBokstavTreff(
        eksakteTreff = eksakteTreff,
        delvisTreff = delvisTreff,
        ordIOppgave = ordIOppgave
    )


    val bokstaverIkkeTruffet = finnBokstaverIkkeTruffet(
        ordIOppgave = ordIOppgave,
        alleTreff = alleTreff
    ).map {
        BokstavTreff(
            plassISekvensen = it,
            bokstavGjettet = ordGjettet[it],
            erBokstavenIOrdet = false,
            erBokstavenPaaRettsted = false
        )
    }

    return (alleTreff + bokstaverIkkeTruffet).sortedBy { it.plassISekvensen }
}

fun slaaSammenEksaktOgDelvisBokstavTreff(
    eksakteTreff: List<BokstavTreff>,
    delvisTreff: List<BokstavTreff>,
    ordIOppgave: String,
): List<BokstavTreff> {
    val bokstavAntallIOrdet: Map<Char, Int> = ordIOppgave
        .groupingBy { it }
        .eachCount()

    val delvisTreffGruppertPaaBokstav = delvisTreff
        .groupBy { it.bokstavGjettet }

    val delvisTreffUtenDuplikater = delvisTreffGruppertPaaBokstav.flatMap { (bokstav, delvisBokstavTreff) ->
        val antallEksaktTreffForBokstav = eksakteTreff.count { it.bokstavGjettet == bokstav }
        val tillattDelvisTreffForBokstav = bokstavAntallIOrdet[bokstav]?.minus(antallEksaktTreffForBokstav) ?: 0
        delvisBokstavTreff.take(tillattDelvisTreffForBokstav)
    }

    return (eksakteTreff + delvisTreffUtenDuplikater)
}

fun finnBokstaverIkkeTruffet(
    ordIOppgave: String,
    alleTreff: List<BokstavTreff>
): List<Int> {
    val alleTreffIndekser = alleTreff.map { it.plassISekvensen }
    val indekserIOrd = (0 until ordIOppgave.length).toList()

    return indekserIOrd.minus(alleTreffIndekser)
}
