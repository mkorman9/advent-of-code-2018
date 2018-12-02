package com.gitlab.mkorman.aoc2018.task02

import java.io.File

// Subtask #1

fun calculateChecksum(ids: List<String>): Int {
    var distinct2Letters = 0
    var distinct3Letters = 0

    for (id in ids) {
        val distinctLetters = countDistinctLetters(id)

        if (hasSpecificLettersCount(distinctLetters, 2)) {
            distinct2Letters++
        }
        if (hasSpecificLettersCount(distinctLetters, 3)) {
            distinct3Letters++
        }
    }

    return distinct2Letters * distinct3Letters
}

private fun countDistinctLetters(id: String): Map<Char, Int> {
    val result = mutableMapOf<Char, Int>()

    for (letter in id) {
        if (!result.containsKey(letter)) {
            result[letter] = 0
        }

        result[letter] = result[letter]!! + 1
    }

    return result.toMap()
}

private fun hasSpecificLettersCount(input: Map<Char, Int>, n: Int): Boolean {
    for ((_, count) in input) {
        if (count == n) {
            return true
        }
    }

    return false
}

// Subtask #2

fun findMatchingLetters(ids: List<String>): String {
    for (id1 in ids) {
        for (id2 in ids) {
            val commonLetters = matchCommonLetters(id1, id2)
            val nonMatchingLettersCount = (id1.length - commonLetters.length)

            if (nonMatchingLettersCount == 1) {
                return commonLetters
            }
        }
    }

    return ""
}

fun matchCommonLetters(id1: String, id2: String): String {
    if (id1.length != id2.length) {
        return ""
    }

    var result = ""

    for (i in 0 until id1.length) {
        if (id1[i] == id2[i]) {
            result += id1[i]
        }
    }

    return result
}

// Main

fun main(args: Array<String>) {
    val source = object {}.javaClass.getResource("/task02/input.txt").file
    val ids = File(source)
        .readLines()

    println("Subtask #1: ${calculateChecksum(ids)}")
    println("Subtask #2: ${findMatchingLetters(ids)}")
}