package kotlinTelegramBot

import java.io.File

fun main() {

    val dictionary = File("words.txt").readLines().map {
        val split = it.split("|")
        Word(split[0], split[1], split[2].toIntOrNull() ?: 0)
    }
    dictionary.forEach {
        println(it)
    }

}

data class Word(
    val original: String,
    val translate: String,
    val correctAnswersCount: Int = 0,
)
