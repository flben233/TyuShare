package util

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers

sealed class HttpUtil {
    companion object Default : HttpUtil()

    fun post(url: String, requestBody: String): HttpResponse<String> {
        val request = HttpRequest.newBuilder(URI.create(url))
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()
        return HttpClient.newHttpClient().send(request, BodyHandlers.ofString())
    }

    fun get(url: String): HttpResponse<String> {
        val request = HttpRequest.newBuilder(URI.create(url))
            .GET()
            .build()
        return HttpClient.newHttpClient().send(request, BodyHandlers.ofString())
    }
}