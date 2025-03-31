package kotlinTelegramBot

import java.io.File

const val LEARNED_WORDS_COUNT = 3
const val TO_LEARN_WORDS_COUNT = 4

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
            "1" -> {
                val notLearnedList = dictionary.filter { it.correctAnswersCount < LEARNED_WORDS_COUNT }
                if (notLearnedList.isEmpty()) {
                    println("Все слова в словаре выучены\n")
                    continue
                }
                learnWords(notLearnedList.shuffled().take(TO_LEARN_WORDS_COUNT))
            }

            "2" -> println(getStatDictionary(dictionary))
            "0" -> return
            else -> println("Введите число 1, 2 или 0")
        }
    }
}

fun learnWords(questionWords: List<Word>) {
    for (correctAnswer in questionWords.indices) {
        println("${questionWords[correctAnswer].original}:")
        for (i in questionWords.indices) {
            println("${i + 1} - ${questionWords[i].translate}")
        }
        readln()
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
