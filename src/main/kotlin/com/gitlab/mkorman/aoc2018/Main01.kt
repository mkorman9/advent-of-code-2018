import java.io.File
import kotlin.coroutines.experimental.buildSequence

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

    return buildSequence {
        for (change in changes) {
            frequency += change
            yield(frequency)
        }
    }
}


fun main(args: Array<String>) {
    val source = object {}.javaClass.getResource("/input.txt").file
    val changes = File(source)
        .readLines()
        .map { it.toInt() }

    println(findRepeatedFrequency(changes))
}
