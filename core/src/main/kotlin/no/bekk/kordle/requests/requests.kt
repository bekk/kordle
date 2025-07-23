package no.bekk.kordle.requests

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import com.badlogic.gdx.net.HttpStatus
import kotlinx.serialization.json.Json
import no.bekk.kordle.shared.dto.GjettOrdRequest
import no.bekk.kordle.shared.dto.GjettResponse

// Callback-based approach
inline fun <reified T, reified R> executeRequest(
    method: String,
    path: String,
    body: T,
    crossinline onSuccess: (R) -> Unit,
    crossinline onError: (String) -> Unit
) {
    val httpRequest = generateHttpRequest(method, path, body)

    Gdx.net.sendHttpRequest(httpRequest, object : Net.HttpResponseListener {
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
        this.content = Json.encodeToString(body)
        // Set content type for JSON
        this.setHeader("Content-Type", "application/json")
    }
}

fun gjettOrd(gjettOrdRequest: GjettOrdRequest) {
    val response = executeRequest<GjettOrdRequest, GjettResponse>(
        method = "POST",
        path = "/gjettOrd",
        body = gjettOrdRequest,
        onSuccess = { response ->
            println("Response: $response")
            // RENDER BOXES HERE
        },
        onError = { error ->
            println("Error occurred: $error")
            // Handle error here
        }
    )
}
