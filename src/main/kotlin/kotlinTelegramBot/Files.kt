package kotlinTelegramBot


fun main() {
    val trainer = LearnWordsTrainer()

    while (true) {
        print("Меню:\n1 – Учить слова\n2 – Статистика\n0 – Выход\n")
        when (readln().toIntOrNull()) {
            1 -> {
                val question = trainer.getNextQuestion()
                if (question == null) {
                    println("Все слова в словаре выучены\n")
                    break
                }
                println(trainer.questionToString(question.variants, question.correctAnswer))
                val userAnswer = readln().toIntOrNull()
                if (userAnswer == 0) break
                if (trainer.checkAnswer(userAnswer?.minus(1))){
                    println("Правильно!")
                }else{
                    println("Неправильно! ${question.correctAnswer.original} – это ${question.correctAnswer.translate}")
                }

            }

            2 -> {
                val statistics = trainer.getStatistics()
                println("Выучено ${statistics.learnedCount} из ${statistics.totalCount} слов ${statistics.percent}%\n")
            }

            0 -> return
            else -> println("Введите число 1, 2 или 0")
        }
    }
}






