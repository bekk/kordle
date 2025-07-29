import org.springframework.web.client.RestClient
import org.springframework.web.client.body

fun main() {
    val suggestClient = SuggestClient()
    suggestClient.suggest()
}

class SuggestClient {

    //private val json = Json { ignoreUnknownKeys = true }
    private val restClient = RestClient.builder().baseUrl("https://ord.uib.no/api").build()

    fun suggest() {
        val responseBody = restClient.get()
            .uri("/suggest?q=____&n=100&dict=bm&include=eis&dform=int")
            .retrieve()

        println(responseBody.body<String>())
        //val parsed = json.decodeFromString<RawSuggestResponse>(responseBody)
    }

    /* fun suggest(
         query: String,
         wordClass: String? = null,
         count: Int = 10,
         dicts: List<Dict> = listOf(Dict.Bokmaal, Dict.Nynorsk),
         include: Set<IncludeFlag> = setOf(
             IncludeFlag.Exact,
             IncludeFlag.Freetext,
             IncludeFlag.Inflect,
             IncludeFlag.Similar
         ),
         dform: Int? = null
     ): Map<ResultType, List<SuggestEntry>> {
         val responseBody = restClient.get()
             .uri { uriBuilder ->
                 uriBuilder
                     .path("/suggest")
                     .queryParam("q", query)
                     .queryParam("n", count)
                     .queryParam("dict", dicts.joinToString(",") { it.code })
                     .queryParam("include", include.joinToString("") { it.code.toString() })
                     .apply {
                         if (wordClass != null) queryParam("wc", wordClass)
                         if (dform != null) queryParam("dform", dform)
                     }
                     .build()
             }
             .retrieve()
             .body(String::class.java)

         val parsed = json.decodeFromString<RawSuggestResponse>(responseBody!!)
         return parsed.parseEntries()
     }*/
}
