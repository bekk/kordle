package no.bekk.kordle.requests

import com.badlogic.gdx.Net
import com.badlogic.gdx.net.HttpStatus

fun generateHttpRequest(
    method: Net.HttpMethods,
    url: String,
    body: String,
){
// Create the HTTP request
    val httpRequest = Net.HttpRequest(method.toString()).apply {
        this.url = url

        // Set the request body
        this.content = """
        {
            "name": "John Doe",
            "email": "john@example.com",
            "age": 30
        }
    """.trimIndent()
    }
}



//// Create response listener
//val responseListener = object : Net.HttpResponseListener {
//    override fun handleHttpResponse(httpResponse: Net.HttpResponse) {
//        val statusCode = httpResponse.status.statusCode
//        val responseBody = httpResponse.resultAsString
//
//        if (statusCode == HttpStatus.SC_OK) {
//            println("Success: $responseBody")
//            // Handle successful response
//        } else {
//            println("Error: $statusCode - $responseBody")
//            // Handle error response
//        }
//    }
//
//    override fun failed(t: Throwable) {
//        println("Request failed: ${t.message}")
//        // Handle network failure
//    }
//
//    override fun cancelled() {
//        println("Request cancelled")
//        // Handle cancellation
//    }
//}
//
//// Send the request
//Gdx.net.sendHttpRequest(httpRequest, responseListener)
