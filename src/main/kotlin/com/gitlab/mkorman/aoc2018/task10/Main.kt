package com.gitlab.mkorman.aoc2018.task10

import java.io.File

data class Point(
    var x: Int,
    var y: Int,
    val velocityX: Int,
    val velocityY: Int
) {
    companion object {
        private val PointPattern = Regex("^position=<(.*), (.*)> velocity=<(.*), (.*)>\$")

        fun parse(input: String): Point {
            val match = PointPattern.find(input) ?: throw IllegalArgumentException("Invalid point '${input}'")

            return Point(
                x = Integer.valueOf(match.groupValues[1].trim()),
                y = Integer.valueOf(match.groupValues[2].trim()),
                velocityX = Integer.valueOf(match.groupValues[3].trim()),
                velocityY = Integer.valueOf(match.groupValues[4].trim())
            )
        }
    }
}

class Map(val points: Set<Point>) {
    fun minimizeSpan(maxIter: Int): Int {
        var localMinimum = Long.MAX_VALUE
        var updates = 0

        for (i in 0 .. maxIter) {
            update()

            val span = calculateSpan()
            if (span > localMinimum) {
                revertUpdate()
                break
            }
            localMinimum = span

            updates++
        }

        return updates
    }

    fun draw() {
        val pixels: Set<Pair<Int, Int>> = points.map { Pair(it.x, it.y) }.toSet()

         for (y in startY until boundY) {
             for (x in startX until boundX) {
                if (pixels.contains(Pair(x, y))) {
                    print("#")
                } else {
                    print(" ")
                }
            }

            println()
        }
    }

    private fun update() {
        for (point in points) {
            point.x += point.velocityX
            point.y += point.velocityY
        }
    }

    private fun revertUpdate() {
        for (point in points) {
            point.x -= point.velocityX
            point.y -= point.velocityY
        }
    }

    private fun calculateSpan(): Long {
        return (boundX + Math.abs(startX)).toLong() * (boundY + Math.abs(startY)).toLong()
    }

    private val startX: Int
        get() = points.minBy { it.x }!!.x

    private val startY: Int
        get() = points.minBy { it.y }!!.y

    private val boundX: Int
        get() = points.maxBy { it.x }!!.x + 1

    private val boundY: Int
        get() = points.maxBy { it.y }!!.y + 1
}

fun main(args: Array<String>) {
    val source = object {}.javaClass.getResource("/task10/input.txt").file
    val points = File(source)
        .readLines()
        .map { Point.parse(it) }
        .toSet()
    val map = Map(points)

    val updatesCount = map.minimizeSpan(maxIter = 1000000)

    println("Subtask #1:")
    map.draw()

    println("Subtask #2: ${updatesCount}")
}
