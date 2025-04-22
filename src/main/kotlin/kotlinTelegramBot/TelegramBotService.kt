package kotlinTelegramBot

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService {

    val updateIdRegex: Regex = "\"update_id\":(.+?),".toRegex()
    val messageRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex: Regex = "\"id\":(.+?),".toRegex()

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

    fun sendMessage(botToken: String, chatId: String?, text: String) {
        val urlSendMess = "https://api.telegram.org/bot$botToken/sendMessage?chat_id=$chatId&text=$text"
        val client = HttpClient.newBuilder().build()
        val response = client.send(
            HttpRequest.newBuilder().uri(URI.create(urlSendMess)).build(),
            HttpResponse.BodyHandlers.ofString()
        )
    }

}