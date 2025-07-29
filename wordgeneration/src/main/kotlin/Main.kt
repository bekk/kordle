import valuetypes.Malform
import valuetypes.ResultType

fun main() {
    val henter = OrdHenterDeluxe3000()
    (4..6).forEach { n ->
        val fasitOrd = henter.getFasitOrdForLength(n)
        println("Fasit ord for lengde $n: ${fasitOrd.size} ord")
        val gjettOrd = henter.getGjettOrdForLength(n)
        println("Gjette-ord for lengde $n: ${gjettOrd.size} ord")
    }
}

class OrdHenterDeluxe3000 {
    private val client = SuggestClient()
    fun getFasitOrdForLength(
        length: Int,
        /* antall ord å hente - merk at dette gjøres alfabetisk.
         Hvis man vil ha et begrenset antall, kan det være bedre å hente ut så mange som overhodet mulig,
         og så gjøre .shuffled().take(n) */
        count: Int = 1_000_000
    ): Set<String> {
        val results = client.suggest(
            // finner ord med lengde n
            "_".repeat(length),
            count = count,
            malform = Malform.Bokmal,
        )
        return results[ResultType.Exact]
            ?.map { word -> word.lemma }
            ?.filter(::isValidOrd)
            ?.toSet()
            ?: emptySet()
    }

    fun getGjettOrdForLength(
        length: Int,     /* antall ord å hente - merk at dette gjøres alfabetisk.
     Hvis man vil ha et begrenset antall, kan det være bedre å hente ut så mange som overhodet mulig,
     og så gjøre .shuffle().take(n) */
        count: Int = 1_000_000
    ): Set<String> {
        val results = client.suggest(
            // finner ord med lengde n
            "_".repeat(length),
            count = count,
            malform = Malform.Bokmal
        )
        return results.values.flatMap { it.map { entry -> entry.lemma } }.toSet()
    }

    val validLetters = ('a'..'z') + ('A'..'Z') + "æøåÆØÅ"
    fun isValidOrd(word: String): Boolean {
        return word.all { it in validLetters }
    }
}
