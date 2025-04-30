package kotlinTelegramBot

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

const val TELEGRAM_URL = "https://api.telegram.org"

class TelegramBotService(val botToken: String) {

    val updateIdRegex: Regex = "\"update_id\":(.+?),".toRegex()
    val messageRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex: Regex = "\"id\":(.+?),".toRegex()
    val dataRegex = "\"data\":\"(.+?)\"".toRegex()
    val client: HttpClient? = HttpClient.newBuilder().build()

    fun getUpdates(updateId: Int): String {
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

    fun sendMessage(chatId: String?, text: String) {
        val encoded = URLEncoder.encode(text, StandardCharsets.UTF_8)
        val urlSendMess = "$TELEGRAM_URL/bot$botToken/sendMessage?chat_id=$chatId&text=$encoded"
        client?.send(
            HttpRequest.newBuilder().uri(URI.create(urlSendMess)).build(),
            HttpResponse.BodyHandlers.ofString()
        )
    }

    fun sendMenu(chatId: String?): String {
        val urlSendMess = "$TELEGRAM_URL/bot$botToken/sendMessage"
        val sendMenuBody = """
            {
                "chat_id": $chatId,
                "text": "Основное меню",
                "reply_markup": {
                    "inline_keyboard": [
                        [
                            {
                                "text": "Изучить слова",
                                "callback_data": "learn_words_clicked"
                            },
                            {
                                "text": "Статистика",
                                "callback_data": "statistic_clicked"
                            }
                        ]
                    ]
                }
            }
        """.trimIndent()
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMess))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()
        val response = client?.send(
            request, HttpResponse.BodyHandlers.ofString()
        )
        return response?.body() ?: "Nothing"
    }

}