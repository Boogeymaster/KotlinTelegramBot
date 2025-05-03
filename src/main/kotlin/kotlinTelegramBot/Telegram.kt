package kotlinTelegramBot

fun main(args: Array<String>) {
    val botToken = args[0]
    val botService = TelegramBotService(botToken)
    var updateId = 0
    val trainer = LearnWordsTrainer()
    while (true) {
        Thread.sleep(2000)
        val updates = botService.getUpdates(updateId)
        updateId = botService.parseFromUpdate(botService.updateIdRegex, updates)?.toInt()?.plus(1) ?: continue
        if (botService.parseFromUpdate(botService.messageRegex, updates).equals("/start")) {
            val chatId = botService.parseFromUpdate(botService.chatIdRegex, updates) ?: continue
            botService.sendMenu(chatId)
        }
        println(botService.parseFromUpdate(botService.dataRegex, updates))
    }
}
