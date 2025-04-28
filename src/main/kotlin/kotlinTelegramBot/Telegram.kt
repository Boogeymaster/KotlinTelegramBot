package kotlinTelegramBot

fun main(args: Array<String>) {
    val botToken = args[0]
    val botService = TelegramBotService()
    var updateId = 0
    while (true) {
        Thread.sleep(2000)
        val updates = botService.getUpdates(botToken, updateId)
        updateId = botService.parseFromUpdate(botService.updateIdRegex, updates)?.toInt()?.plus(1) ?: continue
        if (botService.parseFromUpdate(botService.messageRegex, updates).equals("Hello")) {
            botService.sendMessage(botToken, botService.parseFromUpdate(botService.chatIdRegex, updates), "Hello")
        }
    }
}
