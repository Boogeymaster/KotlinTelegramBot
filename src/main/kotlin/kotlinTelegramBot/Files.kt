package kotlinTelegramBot

import java.io.File

fun main() {
    val dictionary = loadDictionary()
    while (true) {
        println(
            """
        Меню: 
        1 – Учить слова
        2 – Статистика
        0 – Выход
    """.trimIndent()
        )
        when (readln()) {
            "1" -> println("Выбран пункт \"Учить слова\"")
            "2" -> println("Выбран пункт \"Статистика\"")
            "0" -> return
            else -> println("Введите число 1, 2 или 0")
        }
    }
}

fun loadDictionary(): List<Word> {
    return File("words.txt").readLines().map {
        val split = it.split("|")
        Word(split[0], split[1], split[2].toIntOrNull() ?: 0)
    }
}

data class Word(
    val original: String,
    val translate: String,
    val correctAnswersCount: Int = 0,
)
