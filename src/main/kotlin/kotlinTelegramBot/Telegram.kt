package kotlinTelegramBot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.text.substringAfter

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
    val text: String? = null,
    @SerialName("chat")
    val chat: Chat? = null,
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
    val trainers = HashMap<Long, LearnWordsTrainer>()
    val json = Json {
        ignoreUnknownKeys = true
    }
    while (true) {
        val responseString = botService.getUpdates(updateId)
        println(responseString)
        val response: Response = json.decodeFromString(responseString)
        if (response.result.isEmpty()) continue
        val sortedUpdates = response.result.sortedBy { it.updateId }
        sortedUpdates.forEach { handleUpdate(it, botService, trainers) }
        updateId = sortedUpdates.last().updateId + 1
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

fun handleUpdate(update: Update, botService: TelegramBotService, trainers: HashMap<Long, LearnWordsTrainer>) {
    val chatId = update.message?.chat?.id ?: update.callbackQuery?.message?.chat?.id ?: return
    val trainer = trainers.getOrPut(chatId) { LearnWordsTrainer(fileName = "$chatId.txt") }
    val message = update.message?.text
    if (message == "/start") botService.sendMenu(chatId)
    val data = update.callbackQuery?.data
    when (data) {
        LEARN_WORDS_BUTTON -> checkNextQuestionAndSend(trainer, botService, chatId)
        RESET_PROGRESS_BUTTON -> {
            trainer.resetProgress()
            botService.sendMessage(chatId, "Прогресс успешно сброшен")
        }

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
