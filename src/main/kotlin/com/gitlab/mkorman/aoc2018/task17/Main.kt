package com.gitlab.mkorman.aoc2018.task17

import java.io.File

class ClayPosition(val x: Int, val y: Int) {
    companion object {
        private val VerticalStretchPattern = Regex("^y=(\\d+), x=(\\d+)..(\\d+)$");
        private val HorizontalStretchPattern = Regex("^x=(\\d+), y=(\\d+)..(\\d+)$");

        fun parseRecord(record: String): List<ClayPosition> {
            return tryVerticalStretchPattern(record)
                    ?: tryHorizontalStretchPattern(record)
                    ?: throw IllegalArgumentException("invalid record '${record}'")
        }

        private fun tryVerticalStretchPattern(record: String): List<ClayPosition>? {
            val match = VerticalStretchPattern.find(record) ?: return null;
            val result = mutableListOf<ClayPosition>()

            for (i in Integer.valueOf(match.groupValues[2]) until Integer.valueOf(match.groupValues[3])) {
                result.add(ClayPosition(i, Integer.valueOf(match.groupValues[1])))
            }

            return result
        }

        private fun tryHorizontalStretchPattern(record: String): List<ClayPosition>? {
            val match = HorizontalStretchPattern.find(record) ?: return null;
            val result = mutableListOf<ClayPosition>()

            for (i in Integer.valueOf(match.groupValues[2]) until Integer.valueOf(match.groupValues[3])) {
                result.add(ClayPosition(Integer.valueOf(match.groupValues[1]), i))
            }

            return result
        }
    }
}

class Map(clayPositions: List<ClayPosition>) {
    private val area: Array<Array<Tile>>
    private val leftPadding: Int

    init {
        val minX = clayPositions.minBy { it.x }!!.x
        val maxX = clayPositions.maxBy { it.x }!!.x + 1
        val maxY = clayPositions.maxBy { it.y }!!.y + 1
        val verticalSpan = Math.abs(maxX) + Math.abs(minX) + 2

        area = Array(maxY) { y ->
            Array(verticalSpan) { x -> Tile.Sand }
        }

        leftPadding = minX

        for (clay in clayPositions) {
            area[indexY(clay.y)][indexX(clay.x)] = Tile.Clay
        }

        area[indexY(0)][indexX(500)] = Tile.Water
    }

    fun countWaterTiles(): Int {
        var result = 0

        for (y in 0 until area.size) {
            for (x in 0 until area[y].size) {
                if (area[y][x] == Tile.Water) {
                    result++
                }
            }
        }

        return result
    }

    private fun indexX(index: Int) = index + leftPadding
    private fun indexY(index: Int) = index

    enum class Tile {
        Sand,
        Clay,
        Water
    }
}

fun main(args: Array<String>) {
    val source = File(object {}.javaClass.getResource("/task17/input.txt").file);
    val clayPositions: List<ClayPosition> = source.readLines()
        .flatMap { ClayPosition.parseRecord(it) }
    val map = Map(clayPositions)

    println(map.countWaterTiles())
}
