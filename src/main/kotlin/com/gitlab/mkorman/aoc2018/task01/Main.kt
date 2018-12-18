import java.io.File

fun findRepeatedFrequency(changes: List<Int>): Int {
    val encounteredFrequencies = mutableSetOf<Int>()
    var currentFrequency = 0

    while (true) {
        for (frequency in getSubsequentFrequencies(currentFrequency, changes)) {
            if (encounteredFrequencies.contains(frequency)) {
                return frequency
            }

            encounteredFrequencies.add(frequency)
            currentFrequency = frequency
        }
    }
}

private fun getSubsequentFrequencies(initialFrequency: Int, changes: List<Int>): Sequence<Int> {
    var frequency = initialFrequency

    return sequence {
        for (change in changes) {
            frequency += change
            yield(frequency)
        }
    }
}


fun main(args: Array<String>) {
    val source = object {}.javaClass.getResource("/task01/input.txt").file
    val changes = File(source)
        .readLines()
        .map { it.toInt() }

    println("Subtask #1: ${changes.sum()}")
    println("Subtask #2: ${findRepeatedFrequency(changes)}")
}
