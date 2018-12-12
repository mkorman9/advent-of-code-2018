package com.gitlab.mkorman.aoc2018.task12

import java.io.File

data class Rule(
    val pattern: List<Boolean>,
    val result: Boolean
) {
    fun match(inputPattern: List<Boolean>): Boolean {
        if (pattern.size != inputPattern.size) {
            return false
        }

        for (i in pattern.indices) {
            if (pattern[i] != inputPattern[i]) {
                return false
            }
        }

        return true
    }
}

data class Pots(
    val state: List<Boolean>,
    val rules: List<Rule>,
    val startIndex: Int
) {
    fun countIndexes(): Long {
        var sum = 0L

        for (i in 0 until state.size) {
            if (state[i]) {
                sum += (i + startIndex)
            }
        }

        return sum
    }

    override fun toString(): String {
        return state.map { if(it) '#' else '.' }.joinToString("")
    }

    fun generateNextGeneration(): Pots {
        val pots = mutableListOf<Boolean>()

        for (potIndex in -2 until state.size + 2) {
            val potEnvironment = listOf(
                getPot(potIndex - 2),
                getPot(potIndex - 1),
                getPot(potIndex),
                getPot(potIndex + 1),
                getPot(potIndex + 2)
            )

            val newPot = matchRule(potEnvironment)

            pots.add(newPot)
        }

        return Pots(
            state = pots.toList(),
            rules = rules,
            startIndex = startIndex - 2
        )
    }

    private fun getPot(index: Int): Boolean {
        if (index < 0 || index >= state.size) {
            return false
        }

        return state[index]
    }

    private fun matchRule(pattern: List<Boolean>): Boolean {
        for (rule in rules) {
            if (rule.match(pattern)) {
                return rule.result
            }
        }

        //throw IllegalStateException("No rule matching pattern ${pattern}")
        return false
    }

    companion object {
        private val InitialStatePattern = Regex("^initial state: (.*)$")
        private val RulePattern = Regex("^(.*) => (.*)$")

        fun parseFile(file: File): Pots {
            val reader = file.bufferedReader()

            val initialStateLine = reader.readLine()
            val rules = mutableListOf<Rule>()

            reader.readLine()

            while (true) {
                val line = reader.readLine()
                if (line == null) {
                    break
                }

                rules.add(parseRule(line))
            }

            return Pots(
                state = parseInitialState(initialStateLine),
                rules = rules.toList(),
                startIndex = 0
            )
        }

        private fun parseInitialState(str: String): List<Boolean> {
            val match =
                InitialStatePattern.find(str) ?: throw IllegalArgumentException("Invalid initial state line '${str}'")
            return parsePattern(match.groupValues[1])
        }

        private fun parseRule(str: String): Rule {
            val match = RulePattern.find(str) ?: throw IllegalArgumentException("Invalid rule '${str}'")
            return Rule(
                pattern = parsePattern(match.groupValues[1]),
                result = parseSinglePot(match.groupValues[2][0])
            )
        }

        private fun parsePattern(str: String): List<Boolean> = str.map { parseSinglePot(it) }

        private fun parseSinglePot(pot: Char) = when (pot) {
            '#' -> true
            '.' -> false
            else -> throw IllegalArgumentException("Invalid pot state '${pot}'")
        }
    }
}
