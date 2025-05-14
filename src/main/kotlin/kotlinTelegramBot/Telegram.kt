package kotlinTelegramBot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    val result: List<Update>,
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String,
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String? = null,
    @SerialName("message")
    val message: Message? = null,
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long,
)

@Serializable
data class SendMessRequest(
    @SerialName("chat_id")
    val chatId: Long,
    @SerialName("text")
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup? = null,

    )

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboards: List<List<InlineKeyboard>>,
)

@Serializable
data class InlineKeyboard(
    @SerialName("text")
    val text: String,
    @SerialName("callback_data")
    val callbackData: String,

    )

fun main(args: Array<String>) {
    val botToken = args[0]
    val botService = TelegramBotService(botToken)
    var updateId = 0L
    val trainer = LearnWordsTrainer()
    val json = Json {
        ignoreUnknownKeys = true
    }
    while (true) {
        Thread.sleep(2000)
        val responseString = botService.getUpdates(updateId)
        println(responseString)
        val response: Response = json.decodeFromString(responseString)
        val update = response.result
        val firstUpdate = update.firstOrNull() ?: continue
        updateId = firstUpdate.updateId + 1
        val message = firstUpdate.message?.text
        val chatId = firstUpdate.message?.chat?.id ?: firstUpdate.callbackQuery?.message?.chat?.id ?: continue
        if (message == "/start") {
            botService.sendMenu(chatId)
        }
        val data = firstUpdate.callbackQuery?.data
        when (data) {
            LEARN_WORDS_BUTTON -> checkNextQuestionAndSend(trainer, botService, chatId)
            STATISTIC_BUTTON -> {
                val statistics = trainer.getStatistics()
                botService.sendMessage(
                    chatId,
                    "Выучено ${statistics.learnedCount} из ${statistics.totalCount} слов ${statistics.percent}%"
                )
            }
        }
        if (data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true) {
            val userAnswerIndex = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()
            if (trainer.checkAnswer(userAnswerIndex.minus(1))) {
                botService.sendMessage(chatId, "Правильно!")
            } else {
                botService.sendMessage(
                    chatId,
                    "Неправильно! ${trainer.question?.correctAnswer?.original} – это ${trainer.question?.correctAnswer?.translate}"
                )
            }
            checkNextQuestionAndSend(trainer, botService, chatId)
        }
    }
}

fun checkNextQuestionAndSend(
    trainer: LearnWordsTrainer,
    botService: TelegramBotService,
    chatId: Long,
) {
    val question = trainer.getNextQuestion()
    if (question == null) {
        botService.sendMessage(
            chatId,
            "Вы выучили все слова в базе"
        )
    } else {
        botService.sendQuestion(chatId, question)
    }
}
