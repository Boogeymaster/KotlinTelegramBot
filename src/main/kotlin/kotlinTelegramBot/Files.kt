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
                learnWords(notLearnedList.shuffled().take(TO_LEARN_WORDS_COUNT), dictionary)
            }
            "2" -> println(getStatDictionary(dictionary))
            "0" -> return
            else -> println("Введите число 1, 2 или 0")
        }
    }
}

fun learnWords(questionWords: List<Word>, dictionary: List<Word>) {
    for (questionWord in questionWords) {
        val correctAnswerId = questionWords.indexOf(questionWord)
        println(questionWords.mapIndexed { index, word -> "${index + 1} - ${word.translate}" }
            .joinToString("\n", "${questionWord.original}:\n", "\n----------\n0 - Меню\n"))
        when (readln().toInt()) {
            correctAnswerId + 1 -> {
                questionWord.correctAnswersCount++
                saveDictionary(dictionary.map { if (it.original == questionWord.original) questionWord else it })
                println("Правильно!")
            }
            0 -> break
            else -> println("Неправильно! ${questionWord.original} – это ${questionWord.translate}")
        }
    }
}

fun saveDictionary(dictionary: List<Word>) {
    File("words.txt").writeText(dictionary.joinToString("\n")
    { "${it.original}|${it.translate}|${it.correctAnswersCount}" })
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
    var correctAnswersCount: Int = 0,
)
