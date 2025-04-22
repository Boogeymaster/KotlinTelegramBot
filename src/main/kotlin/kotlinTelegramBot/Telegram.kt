package kotlinTelegramBot

fun main(args: Array<String>) {
    val botToken = args[0]
    val adapter = TelegramBotService()
    var updateId = 0
    while (true) {
        Thread.sleep(2000)
        val updates = adapter.getUpdates(botToken, updateId)
        updateId = adapter.parseFromUpdate(adapter.updateIdRegex, updates)?.toInt()?.plus(1) ?: continue
        if (adapter.parseFromUpdate(adapter.messageRegex, updates).equals("Hello")) {
            adapter.sendMessage(botToken, adapter.parseFromUpdate(adapter.chatIdRegex, updates), "Hello")
        }
    }
}
