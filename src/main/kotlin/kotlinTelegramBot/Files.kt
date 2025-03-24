package kotlinTelegramBot

import java.io.File

fun main() {

    val wordsFile = File("words.txt")
    wordsFile.writeText("hello привет\ndog собака\ncat кошка")
    val pairs = wordsFile.readLines()
    pairs.forEach {
        println(it)
    }

}

//Вторая попытка