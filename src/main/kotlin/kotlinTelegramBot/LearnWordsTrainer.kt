package kotlinTelegramBot

import java.io.File

const val LEARNED_WORDS_COUNT = 3
const val TO_LEARN_WORDS_COUNT = 4

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

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer {

    val dictionary = loadDictionary()
    private var question: Question? = null

    fun getStatistics(): Statistics {
        val learnedCount = dictionary.filter { it.correctAnswersCount >= LEARNED_WORDS_COUNT }.size
        val totalCount = dictionary.count()
        val percent = learnedCount * 100 / totalCount
        return Statistics(learnedCount, totalCount, percent)
    }

    fun getNextQuestion(): Question? {
        val notLearnedList = dictionary.filter { it.correctAnswersCount < LEARNED_WORDS_COUNT }
        if (notLearnedList.isEmpty()) return null
        val variants = notLearnedList.shuffled().take(TO_LEARN_WORDS_COUNT)
        val correctAnswer = variants.random()
        val question = Question(variants, correctAnswer)
        return question
    }

    fun checkAnswer(userAnswer: Int?): Boolean {
        return question?.let {
            val correctAnswerId = it.variants.indexOf(it.correctAnswer)
            if (userAnswer == correctAnswerId) {
                it.correctAnswer.correctAnswersCount++
                saveDictionary(dictionary)
                true
            } else false
        } ?: false
    }

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

    fun questionToString(questionWords: List<Word>, questionWord: Word): String {
        return questionWords.mapIndexed { index, word -> "${index + 1} - ${word.translate}" }
            .joinToString("\n", "${questionWord.original}:\n", "\n----------\n0 - Меню\n")
    }

}


