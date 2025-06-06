package kotlinTelegramBot

import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val TELEGRAM_URL = "https://api.telegram.org"
const val LEARN_WORDS_BUTTON = "learn_words_clicked"
const val STATISTIC_BUTTON = "statistic_clicked"
const val RESET_PROGRESS_BUTTON = "reset_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

class TelegramBotService(val botToken: String) {

    val client: HttpClient? = HttpClient.newBuilder().build()
    val json = Json {
        ignoreUnknownKeys = true
    }

    fun getUpdates(updateId: Long): String {
        val urlGetUpdates = "$TELEGRAM_URL/bot$botToken/getUpdates?offset=$updateId"
        val response = client?.send(
            HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build(),
            HttpResponse.BodyHandlers.ofString()
        )
        return response?.body() ?: ""
    }

    fun sendMessage(chatId: Long, text: String) {
        val urlSendMess = "$TELEGRAM_URL/bot$botToken/sendMessage"
        val requestBody = SendMessRequest(
            chatId = chatId,
            text = text,
        )
        val requestBodyString = json.encodeToString(requestBody)
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMess))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        client?.send(
            request, HttpResponse.BodyHandlers.ofString()
        )
    }

    fun sendMenu(chatId: Long) {
        val urlSendMess = "$TELEGRAM_URL/bot$botToken/sendMessage"
        val requestBody = SendMessRequest(
            chatId = chatId,
            text = "Основное меню",
            replyMarkup = ReplyMarkup(
                listOf(
                    listOf(
                        InlineKeyboard(
                            text = "Изучать слова",
                            callbackData = LEARN_WORDS_BUTTON
                        ),
                        InlineKeyboard(
                            text = "Статистика",
                            callbackData = STATISTIC_BUTTON
                        )
                    ),
                    listOf(
                        InlineKeyboard(
                            text = "Сбросить статистику",
                            callbackData = RESET_PROGRESS_BUTTON
                        )
                    )
                )
            )
        )
        val requestBodyString = json.encodeToString(requestBody)
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMess))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        client?.send(
            request, HttpResponse.BodyHandlers.ofString()
        )
    }

    fun sendQuestion(chatId: Long, question: Question) {
        val urlSendMess = "$TELEGRAM_URL/bot$botToken/sendMessage"
        val requestBody = SendMessRequest(
            chatId = chatId,
            text = question.correctAnswer.original,
            replyMarkup = ReplyMarkup(
                listOf(
                    question.variants.mapIndexed { index, word ->
                        InlineKeyboard(
                            text = "${index + 1} - ${word.translate}",
                            callbackData = "$CALLBACK_DATA_ANSWER_PREFIX${index + 1}"
                        )
                    }
                )
            )
        )
        val requestBodyString = json.encodeToString(requestBody)
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMess))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        client?.send(
            request, HttpResponse.BodyHandlers.ofString()
        )
    }
}