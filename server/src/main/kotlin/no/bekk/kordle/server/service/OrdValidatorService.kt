package no.bekk.kordle.server.service

import org.springframework.stereotype.Service

@Service
class OrdValidatorService {
    private val wordsByLength = mutableMapOf<Int, Set<String>>()

    fun isValid(word: String): Boolean {
        val words = wordsByLength.computeIfAbsent(word.length) { loadWordsForLength(it) }
        return words.contains(word.uppercase())
    }

    private fun loadWordsForLength(length: Int): Set<String> {
        val resource = javaClass.getResourceAsStream("/gjettord-$length.txt") ?: return emptySet()
        return resource.bufferedReader().useLines { lines ->
            lines.map { it.trim().uppercase() }.toSet()
        }
    }
}
