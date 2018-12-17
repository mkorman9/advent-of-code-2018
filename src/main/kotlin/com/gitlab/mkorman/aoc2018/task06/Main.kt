package com.gitlab.mkorman.aoc2018.task06

import java.io.File

data class Coordinates(
    val x: Int,
    val y: Int
) {
    companion object {
        private val CoordinatesPattern = Regex("^(\\d+), (\\d+)$")

        fun parseCoordinates(line: String): Coordinates {
            val match = CoordinatesPattern.find(line) ?: throw IllegalArgumentException("Invalid coordinates '${line}'")
            return Coordinates(
                x = Integer.valueOf(match.groupValues[1]),
                y = Integer.valueOf(match.groupValues[2])
            )
        }
    }
}

data class GridField(
    val x: Int,
    val y: Int,
    val distances: MutableMap<Coordinates, Int> = mutableMapOf()
) {
    fun saveDistanceTo(coordinates: Coordinates) {
        distances[coordinates] = Math.abs(x - coordinates.x) + Math.abs(y - coordinates.y)
    }

    fun getOwner(): Coordinates? {
        val closestCoordinates = distances.minBy { it.value }!!
        val closestFieldsCount = distances.count { it.value == closestCoordinates.value }

        return if (closestFieldsCount > 1) null else closestCoordinates.key
    }

    fun isCloserToAllThan(max: Int): Boolean {
        return distances.values.sum() <= max
    }
}

class Grid(val width: Int, val height: Int) {
    private val fields = Array(width * height) { index -> GridField(indexX(index), indexY(index)) }

    fun applyCoordinates(coordinates: Coordinates) {
        fields.forEach { it.saveDistanceTo(coordinates) }
    }

    fun getLargestOwnedArea(): Int {
        val areas = mutableMapOf<Coordinates, Int>()

        for (field in fields) {
            val owner = field.getOwner()
            if (owner != null) {
                areas[owner] = if (areas.containsKey(owner)) areas[owner]!! + 1 else 1
            }
        }

        val finiteAreas = excludeInfiniteAreas(areas)

        return finiteAreas
            .maxBy { it.value }!!
            .value
    }

    fun getRegionSizeWithMaxDistance(maxDistance: Int): Int {
        return fields
            .filter { it.isCloserToAllThan(maxDistance) }
            .count()
    }

    private fun excludeInfiniteAreas(areas: Map<Coordinates, Int>): Map<Coordinates, Int> {
        val result = areas.toMutableMap()

        for (x in 0 until width) {
            result.remove(fields[index(x, 0)].getOwner())
            result.remove(fields[index(x, height - 1)].getOwner())
        }

        for (y in 0 until height) {
            result.remove(fields[index(0, y)].getOwner())
            result.remove(fields[index(width - 1, y)].getOwner())
        }

        return result.toMap()
    }

    private fun index(x: Int, y: Int): Int = y * width + x
    private fun indexX(index: Int): Int = index % width
    private fun indexY(index: Int): Int = index / width
}

fun main(args: Array<String>) {
    val source = object {}.javaClass.getResource("/task06/input.txt").file
    val coordinates = File(source)
        .readLines()
        .map { Coordinates.parseCoordinates(it) }

    val horizontalBoundary = coordinates.maxBy { it.x }!!.x + 1
    val verticalBoundary = coordinates.maxBy { it.y }!!.y + 1

    val grid = Grid(horizontalBoundary, verticalBoundary)
    coordinates.forEach { grid.applyCoordinates(it) }

    println("Subtask #1: ${grid.getLargestOwnedArea()}")
    println("Subtask #2: ${grid.getRegionSizeWithMaxDistance(10000)}")
}
