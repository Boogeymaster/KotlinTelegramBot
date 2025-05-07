package kotlinTelegramBot

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

const val TELEGRAM_URL = "https://api.telegram.org"
const val LEARN_WORDS_BUTTON = "learn_words_clicked"
const val STATISTIC_BUTTON = "statistic_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

class TelegramBotService(val botToken: String) {

    val updateIdRegex: Regex = "\"update_id\":(.+?),".toRegex()
    val messageRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(.+?),".toRegex()
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

    fun sendMessage(chatId: String, text: String) {
        val encoded = URLEncoder.encode(text, StandardCharsets.UTF_8)
        val urlSendMess = "$TELEGRAM_URL/bot$botToken/sendMessage?chat_id=$chatId&text=$encoded"
        client?.send(
            HttpRequest.newBuilder().uri(URI.create(urlSendMess)).build(),
            HttpResponse.BodyHandlers.ofString()
        )
    }

    fun sendMenu(chatId: String) {
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
                                "callback_data": "$LEARN_WORDS_BUTTON"
                            },
                            {
                                "text": "Статистика",
                                "callback_data": "$STATISTIC_BUTTON"
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
        client?.send(
            request, HttpResponse.BodyHandlers.ofString()
        )
    }

    fun sendQuestion(chatId: String, question: Question) {
        val urlSendMess = "$TELEGRAM_URL/bot$botToken/sendMessage"
        val inlineKeyboardBody = question.variants.mapIndexed { index, word ->
            """[
                    {
                        "text": "${index + 1} - ${word.translate}",
                        "callback_data": "$CALLBACK_DATA_ANSWER_PREFIX${index + 1}"
                    }
                ]""".trimIndent()
        }.joinToString(
            ",\n",
            """"inline_keyboard": [
                        """.trimIndent(),
            """
                    ]""".trimIndent()
        )

        val sendMenuBody = """
            {
                "chat_id": $chatId,
                "text": "${question.correctAnswer.original}",
                "reply_markup": {
                    $inlineKeyboardBody
                }
            }
        """.trimIndent()
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMess))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()
        client?.send(
            request, HttpResponse.BodyHandlers.ofString()
        )
    }
}