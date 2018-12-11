package com.gitlab.mkorman.aoc2018.task11

import kotlin.math.max

data class Area(
    val x: Int,
    val y: Int,
    val size: Int,
    val totalPower: Int
)

class Grid(val serialNumber: Int) {
    private val array = Array(Height) { y -> Array(Width) { x -> calculatePowerLevel(x + 1, y + 1) } }

    fun findMaxPowerArea(): Area {
        var maxPowerArea = Area(-1, -1, -1, Int.MIN_VALUE)

        for (size in 1 .. Width) {
            val area = findMaxPowerArea(size, size)
            if (area.totalPower > maxPowerArea.totalPower) {
                maxPowerArea = area
            }
        }

        return maxPowerArea
    }

    fun findMaxPowerArea(areaWidth: Int, areaHeight: Int): Area {
        var maxPowerArea = Area(-1, -1, -1, Int.MIN_VALUE)

        for (x in 0 until (Width - areaWidth)) {
            for (y in 0 until (Height - areaHeight)) {
                val power = getAreaTotalPower(x, y, areaWidth, areaHeight)
                if (power > maxPowerArea.totalPower) {
                    maxPowerArea = Area(x + 1, y + 1, areaWidth, power)
                }
            }
        }

        return maxPowerArea
    }

    private fun getAreaTotalPower(x: Int, y: Int, w: Int, h: Int): Int {
        var power = 0

        for (i in x until (x + w)) {
            for (j in y until (y + h)) {
                power += array[j][i]
            }
        }

        return power
    }

    private fun calculatePowerLevel(x: Int, y: Int): Int {
        val rackId = x + 10
        val powerLevel = ((rackId * y) + serialNumber) * rackId
        return ((powerLevel / 100) % 10) - 5
    }

    companion object {
        const val Width = 300
        const val Height = 300
    }
}

fun main(args: Array<String>) {
    val input = 5093
    val grid = Grid(input)

    println("Subtask #1: ${grid.findMaxPowerArea(3, 3)}")
    println("Subtask #2: ${grid.findMaxPowerArea()}")
}
