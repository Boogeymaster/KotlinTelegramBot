package kotlinTelegramBot

import java.io.File
const val LEARNED_WORDS_COUNT = 3

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
            "2" -> println(getStatDictionary(dictionary))
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

fun getStatDictionary(dictionary: List<Word>): String {
    val learnedWordsList = dictionary.filter { it.correctAnswersCount >= LEARNED_WORDS_COUNT }
    val totalCount = dictionary.count()
    val learnedCount = learnedWordsList.count()
    val percent = learnedCount * 100 / totalCount
    return "Выучено $learnedCount из $totalCount слов | $percent%\n"
}

data class Word(
    val original: String,
    val translate: String,
    val correctAnswersCount: Int = 0,
)
