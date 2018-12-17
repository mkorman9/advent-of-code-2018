package com.gitlab.mkorman.aoc2018.task05

import java.io.File

tailrec fun reactPolymers(input: String): String {
    var result = ""
    var i = 0

    while (i < input.length) {
        val polymer1 = input[i]
        val polymer2 = input.getOrNull(i + 1)

        if (polymer2 == null) {
            result += polymer1
            break
        }

        val polymersReduce = polymer1.equals(polymer2, ignoreCase = true) &&
                ((polymer1.isUpperCase() && !polymer2.isUpperCase()) ||
                        (!polymer1.isUpperCase() && polymer2.isUpperCase()))

        if (polymersReduce) {
            i++
        } else {
            result += polymer1
        }

        i++
    }

    if (input == result) {
        return result
    } else {
        return reactPolymers(result)
    }
}

fun chooseShortestReactedPolymerLength(input: String): Int {
    val lengths = mutableSetOf<Int>()

    for (polymerType in "abcdefghijklmnopqrstuvwxyz") {
        val reducedInput = input.replace(Regex("[${polymerType}${polymerType.toUpperCase()}]"), "")
        lengths.add(reactPolymers(reducedInput).length)
    }

    return lengths.min()!!
}

fun main(args: Array<String>) {
    val source = object {}.javaClass.getResource("/task05/input.txt").file
    val input = File(source).readText()

    println("Subtask #1: ${reactPolymers(input).length}")
    println("Subtask #2: ${chooseShortestReactedPolymerLength(input)}")
}
