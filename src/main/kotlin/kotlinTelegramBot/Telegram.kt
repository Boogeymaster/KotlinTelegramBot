package kotlinTelegramBot

fun main(args: Array<String>) {
    val botToken = args[0]
    val botService = TelegramBotService(botToken)
    var updateId = 0
    var chatId = ""
    val trainer = try {
        LearnWordsTrainer()
    } catch (e: Exception) {
        println("Не возможно загрузить словарь")
        return
    }
    while (true) {
        Thread.sleep(2000)
        val updates = botService.getUpdates(updateId)
        updateId = botService.parseFromUpdate(botService.updateIdRegex, updates)?.toInt()?.plus(1) ?: continue
        if (botService.parseFromUpdate(botService.messageRegex, updates).equals("/start")) {
            chatId = botService.parseFromUpdate(botService.chatIdRegex, updates) ?: continue
            botService.sendMenu(chatId)
        }
        when (botService.parseFromUpdate(botService.dataRegex, updates)) {
            LEARN_WORDS_BUTTON -> {
                TODO()
            }

            STATISTIC_BUTTON -> {
                val statistics = trainer.getStatistics()
                botService.sendMessage(
                    chatId,
                    "Выучено ${statistics.learnedCount} из ${statistics.totalCount} слов ${statistics.percent}%"
                )
            }
        }
    }
}
