package com.gitlab.mkorman.aoc2018.task09

import java.io.File

data class Marble(
    val value: Int,
    var next: Marble? = null,
    var prev: Marble? = null
)

class MarblesCircle {
    var marblesBegin: Marble? = null
    var marblesEnd: Marble? = null
    var currentMarble: Marble? = null

    fun placeMarble(marble: Int): Int {
        if (marblesBegin == null) {
            marblesBegin = Marble(marble)
            marblesEnd = marblesBegin
            currentMarble = marblesBegin
        } else {
            if (marble % 23 == 0) {
                val marbleToRemove = stepsCounterClockwise(7)
                val score = marble + marbleToRemove.value

                currentMarble = marbleToRemove.next

                if (marbleToRemove.prev != null) {
                    marbleToRemove.prev!!.next = marbleToRemove.next
                } else {
                    marblesBegin = marbleToRemove.next
                }

                return score
            }

            val positionToInsert = stepsClockwise(1)
            val tmp = positionToInsert.next
            positionToInsert.next = Marble(marble, tmp, positionToInsert)
            if (tmp != null) {
                tmp.prev = positionToInsert.next
            }
            currentMarble = positionToInsert.next
            if (currentMarble!!.next == null) {
                marblesEnd = currentMarble
            }
        }

        return 0
    }

    private fun stepsClockwise(n: Int): Marble {
        var marble: Marble? = currentMarble

        for (i in 0 until n) {
            marble = marble!!.next
            if (marble == null) {
                marble = marblesBegin
            }
        }

        return marble!!
    }

    private fun stepsCounterClockwise(n: Int): Marble {
        var marble: Marble? = currentMarble

        for (i in 0 until n) {
            marble = marble!!.prev
            if (marble == null) {
                marble = marblesEnd
            }
        }

        return marble!!
    }
}

class MarblesGame(val maxPlayers: Int, val lastMarbleWorth: Int) {
    val circle = MarblesCircle()

    fun play(): Long {
        var player = 0
        val scores = mutableMapOf<Int, Long>()

        for (roundNumber in 0 until (lastMarbleWorth + 1)) {
            val score = circle.placeMarble(roundNumber).toLong()
            scores[player] = if (!scores.containsKey(player)) score else scores[player]!! + score

            player++
            if (player >= maxPlayers) {
                player = 0
            }
        }

        return scores
            .maxBy { it.value }!!
            .value
    }
}

data class GameParameters(
    val maxPlayers: Int,
    val lastMarbleWorth: Int
)

fun parseInput(input: String): GameParameters {
    val regex = Regex("^(\\d+) players; last marble is worth (\\d+) points$")
    val result = regex.find(input) ?: throw IllegalArgumentException("Invalid input '${input}'")

    return GameParameters(
        maxPlayers = Integer.valueOf(result.groupValues[1]),
        lastMarbleWorth = Integer.valueOf(result.groupValues[2])
    )
}

fun main(args: Array<String>) {
    val source = object {}.javaClass.getResource("/task09/input.txt").file
    val input = File(source).readText()
    val parameters = parseInput(input)

    println("Subtask #1: ${MarblesGame(parameters.maxPlayers, parameters.lastMarbleWorth).play()}")
    println("Subtask #1: ${MarblesGame(parameters.maxPlayers, parameters.lastMarbleWorth * 100).play()}")
}
