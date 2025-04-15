package kotlinTelegramBot

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {
    val botToken = args[0]
    val urlGetMe = "https://api.telegram.org/bot$botToken/getMe"
    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates"
    println("${getResponse(urlGetMe)}\n ${getResponse(urlGetUpdates)}")
}

fun getResponse(url: String) : String{
    val client = HttpClient.newBuilder().build()
    val response = client.send(HttpRequest.newBuilder().uri(URI.create(url)).build(), HttpResponse.BodyHandlers.ofString())
    return response.body()
}