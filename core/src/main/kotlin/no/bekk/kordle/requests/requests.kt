package no.bekk.kordle.requests

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import com.badlogic.gdx.net.HttpStatus
import kotlinx.serialization.json.Json
import no.bekk.kordle.shared.dto.GjettOrdRequest
import no.bekk.kordle.shared.dto.GjettResponse
import no.bekk.kordle.shared.dto.OppgaveResponse

// Callback-based approach
inline fun <reified T, reified R> executeRequest(
    request: Net.HttpRequest,
    crossinline onSuccess: (R) -> Unit,
    crossinline onError: (String) -> Unit
) {

    Gdx.net.sendHttpRequest(request, object : Net.HttpResponseListener {
        override fun handleHttpResponse(httpResponse: Net.HttpResponse) {
            val statusCode = httpResponse.status.statusCode
            val responseBody = httpResponse.resultAsString

            if (statusCode == HttpStatus.SC_OK) {
                try {
                    val result = Json.decodeFromString<R>(responseBody)
                    onSuccess(result)
                } catch (e: Exception) {
                    onError("Failed to parse response: ${e.message}")
                }
            } else {
                onError("HTTP Error: $statusCode - $responseBody")
            }
        }

        override fun failed(t: Throwable?) {
            onError("Request failed: ${t?.message ?: "Unknown error"}")
        }

        override fun cancelled() {
            onError("Request was cancelled")
        }
    })
}

inline fun <reified T> generateHttpRequest(
    method: String,
    path: String,
    body: T,
): Net.HttpRequest {
    return Net.HttpRequest(method).apply {
        this.url = "http://localhost:8080$path"
        if (body != null) {
            this.content = Json.encodeToString(body)
        }
        // Set content type for JSON
        this.setHeader("Content-Type", "application/json")
    }
}

fun generateHttpRequest(
    method: String,
    path: String,
): Net.HttpRequest {
    return Net.HttpRequest(method).apply {
        this.url = "http://localhost:8080$path"
        // Set content type for JSON
        this.setHeader("Content-Type", "application/json")
    }
}

fun getTilfeldigOppgave(
    onSuccess: (OppgaveResponse) -> Unit,
) {
    val request = generateHttpRequest("GET", "/hentTilfeldigOppgave")

    executeRequest<Unit, OppgaveResponse>(
        request,
        onSuccess = { response ->
            onSuccess(response)
        },
        onError = { error ->
            println("Error occurred: $error")
            // Handle error here
        }
    )
}

fun gjettOrd(
    gjettOrdRequest: GjettOrdRequest,
    onSuccess: (GjettResponse) -> Unit
) {
    val request = generateHttpRequest("POST", "/gjettOrd", gjettOrdRequest)

    val response = executeRequest<GjettOrdRequest, GjettResponse>(
        request,
        onSuccess = { response ->
            onSuccess(response)
        },
        onError = { error ->
            println("Error occurred: $error")
            // Handle error here
        }
    )
}
