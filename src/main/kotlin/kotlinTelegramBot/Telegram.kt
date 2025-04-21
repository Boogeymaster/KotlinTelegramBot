package kotlinTelegramBot

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {
    val botToken = args[0]
    var updateId = 0
    val updateIdRegex: Regex = "\"update_id\":(.+?),".toRegex()
    val messageRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    while (true) {
        Thread.sleep(2000)
        val updates = getUpdates(botToken, updateId)
        println(updates)
        updateId = parseFromUpdate(updateIdRegex, updates)?.toInt()?.plus(1) ?: continue
        println("${parseFromUpdate(messageRegex, updates)}")
    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
    val client = HttpClient.newBuilder().build()
    val response = client.send(
        HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build(),
        HttpResponse.BodyHandlers.ofString()
    )
    return response.body()
}

fun parseFromUpdate(regex: Regex, updates: String): String? {
    val matchResult = regex.find(updates)
    val groups = matchResult?.groups
    return groups?.get(1)?.value
}
