package kotlinTelegramBot

fun main(args: Array<String>) {
    val botToken = args[0]
    val botService = TelegramBotService(botToken)
    var updateId = 0
    var chatId: String
    val trainer = LearnWordsTrainer()
    while (true) {
        Thread.sleep(2000)
        val updates = botService.getUpdates(updateId)
        updateId = botService.parseFromUpdate(botService.updateIdRegex, updates)?.toInt()?.plus(1) ?: continue
        chatId = botService.parseFromUpdate(botService.chatIdRegex, updates) ?: continue
        if (botService.parseFromUpdate(botService.messageRegex, updates).equals("/start")) {
            botService.sendMenu(chatId)
        }
        val data = botService.parseFromUpdate(botService.dataRegex, updates) ?: continue
        when (botService.parseFromUpdate(botService.dataRegex, updates)) {
            LEARN_WORDS_BUTTON -> {
                checkNextQuestionAndSend(trainer, botService, chatId)
            }

            STATISTIC_BUTTON -> {
                val statistics = trainer.getStatistics()
                botService.sendMessage(
                    chatId,
                    "Выучено ${statistics.learnedCount} из ${statistics.totalCount} слов ${statistics.percent}%"
                )
            }
        }
        if (data.startsWith(CALLBACK_DATA_ANSWER_PREFIX)){
            val userAnswerIndex = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()
            if (trainer.checkAnswer(userAnswerIndex.minus(1))) {
                botService.sendMessage(
                    chatId,
                    "Правильно!"
                )
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
    chatId: String,
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
