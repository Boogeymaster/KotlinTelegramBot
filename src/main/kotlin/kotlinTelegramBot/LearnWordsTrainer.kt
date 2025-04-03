package kotlinTelegramBot

import java.io.File

const val LEARNED_WORDS_COUNT = 3
const val TO_LEARN_WORDS_COUNT = 4

class LearnWordsTrainer {

    val dictionary = loadDictionary()

    private fun saveDictionary(dictionary: List<Word>) {
        File("words.txt").writeText(
            dictionary.joinToString("\n")
            { "${it.original}|${it.translate}|${it.correctAnswersCount}" })
    }

    private fun loadDictionary(): List<Word> {
        return File("words.txt").readLines().map {
            val split = it.split("|")
            Word(split[0], split[1], split[2].toIntOrNull() ?: 0)
        }
    }

    private fun questionToString(questionWords: List<Word>, questionWord: Word): String {
        return questionWords.mapIndexed { index, word -> "${index + 1} - ${word.translate}" }
            .joinToString("\n", "${questionWord.original}:\n", "\n----------\n0 - Меню\n")
    }

    fun getStatistics(): Statistics {
        val learnedCount = dictionary.filter { it.correctAnswersCount >= LEARNED_WORDS_COUNT }.size
        val totalCount = dictionary.count()
        val percent = learnedCount * 100 / totalCount
        return Statistics(learnedCount, totalCount, percent)
    }

    fun getNextQuestion(): List<Word>? {
        val notLearnedList = dictionary.filter { it.correctAnswersCount < LEARNED_WORDS_COUNT }
        return if (notLearnedList.isEmpty()) null
        else notLearnedList.shuffled().take(TO_LEARN_WORDS_COUNT)
    }

    fun learnWords(questionWords: List<Word>, dictionary: List<Word>) {
        for (questionWord in questionWords) {
            val correctAnswerId = questionWords.indexOf(questionWord)
            print(questionToString(questionWords, questionWord))
            when (readln().toInt()) {
                correctAnswerId + 1 -> {
                    questionWord.correctAnswersCount++
                    saveDictionary(dictionary.map { if (it.original == questionWord.original) questionWord else it })
                    println("Правильно!\n")
                }

                0 -> break
                else -> println("Неправильно! ${questionWord.original} – это ${questionWord.translate}")
            }
        }
    }
}

data class Statistics(
    val totalCount: Int,
    val learnedCount: Int,
    val percent: Int,
)

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0,
)
