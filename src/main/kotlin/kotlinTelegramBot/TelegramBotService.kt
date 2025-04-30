package kotlinTelegramBot

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
const val TELEGRAM_URL = "https://api.telegram.org"

class TelegramBotService {

    val updateIdRegex: Regex = "\"update_id\":(.+?),".toRegex()
    val messageRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex: Regex = "\"id\":(.+?),".toRegex()
    val client: HttpClient? = HttpClient.newBuilder().build()

    fun getUpdates(botToken: String, updateId: Int): String {
        val urlGetUpdates = "$TELEGRAM_URL/bot$botToken/getUpdates?offset=$updateId"
        val response = client?.send(
            HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build(),
            HttpResponse.BodyHandlers.ofString()
        )
        return response?.body() ?: ""
    }

    fun parseFromUpdate(regex: Regex, updates: String): String? {
        val matchResult = regex.find(updates)
        val groups = matchResult?.groups
        return groups?.get(1)?.value
    }

    fun sendMessage(botToken: String, chatId: String?, text: String) {
        val urlSendMess = "$TELEGRAM_URL/bot$botToken/sendMessage?chat_id=$chatId&text=$text"
        val response = client?.send(
            HttpRequest.newBuilder().uri(URI.create(urlSendMess)).build(),
            HttpResponse.BodyHandlers.ofString()
        )
    }

}