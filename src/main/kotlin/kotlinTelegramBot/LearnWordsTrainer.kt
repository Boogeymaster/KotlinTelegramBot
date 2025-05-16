package kotlinTelegramBot

import kotlinx.serialization.Serializable
import java.io.File

data class Statistics(
    val learnedCount: Int,
    val totalCount: Int,
    val percent: Int,
)

@Serializable
data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0,
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer(
    private val learnedWordsCount: Int = 3,
    private val toLearnWordsCount: Int = 4,
    private val fileName: String = "words.txt"
) {

    var question: Question? = null
    val dictionary = loadDictionary()


    fun getStatistics(): Statistics {
        val learnedCount = dictionary.count { it.correctAnswersCount >= learnedWordsCount }
        val totalCount = dictionary.count()
        val percent = learnedCount * 100 / totalCount
        return Statistics(learnedCount, totalCount, percent)
    }

    fun getNextQuestion(): Question? {
        val notLearnedList = dictionary.filter { it.correctAnswersCount < learnedWordsCount }
        if (notLearnedList.isEmpty()) return null
        val variants: List<Word> = if (notLearnedList.count() < toLearnWordsCount) {
            val diff = toLearnWordsCount.minus(notLearnedList.count())
            val learnedList = dictionary.shuffled().take(diff)
            notLearnedList.take(toLearnWordsCount) + learnedList.take(diff)
        } else {
            notLearnedList.shuffled().take(toLearnWordsCount)
        }
        val correctAnswer = variants.random()
        question = Question(variants = variants, correctAnswer = correctAnswer)
        return question
    }

    fun checkAnswer(userAnswer: Int?): Boolean {
        return question?.let {
            val correctAnswerId = it.variants.indexOf(it.correctAnswer)
            if (userAnswer == correctAnswerId) {
                it.correctAnswer.correctAnswersCount++
                saveDictionary()
                true
            } else false
        } ?: false
    }

    private fun saveDictionary() {
        File(fileName).writeText(
            dictionary.joinToString("\n")
            { "${it.original}|${it.translate}|${it.correctAnswersCount}" })
    }

    private fun loadDictionary(): List<Word> {
        val wordsFile = File(fileName)
        if (!wordsFile.exists()) File("words.txt").copyTo(wordsFile)
        return File(fileName).readLines().map {
            val split = it.split("|")
            Word(split[0], split[1], split[2].toIntOrNull() ?: 0)
        }
    }

    fun questionToString(questionWords: List<Word>, questionWord: Word): String {
        return questionWords.mapIndexed { index, word -> "${index + 1} - ${word.translate}" }
            .joinToString("\n", "${questionWord.original}:\n", "\n----------\n0 - Меню\n")
    }

    fun resetProgress() {
        dictionary.forEach { it.correctAnswersCount = 0 }
        saveDictionary()
    }
}


