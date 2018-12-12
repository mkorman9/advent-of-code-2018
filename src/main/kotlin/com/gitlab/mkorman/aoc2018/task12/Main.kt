package com.gitlab.mkorman.aoc2018.task12

import java.io.File

fun countIndexesInGeneration(pots: Pots, n: Long): Long {
    var p = pots.copy()

    for (i in 0 until n) {
        p = p.generateNextGeneration()
    }

    return p.countIndexes()
}

fun main(args: Array<String>) {
    val file = File(object {}.javaClass.getResource("/task12/input.txt").file)
    val pots = Pots.parseFile(file)

    println("Subtask #1: ${countIndexesInGeneration(pots, 20)}")

    // after certain index sum changes by exactly 52 per each generation
    println("Subtask #2: ${countIndexesInGeneration(pots, 1000) + ((50000000000 - 1000) * 52)}")
}
