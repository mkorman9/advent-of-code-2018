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

            for (i in Integer.valueOf(match.groupValues[2])..Integer.valueOf(match.groupValues[3])) {
                result.add(ClayPosition(i, Integer.valueOf(match.groupValues[1])))
            }

            return result
        }

        private fun tryHorizontalStretchPattern(record: String): List<ClayPosition>? {
            val match = HorizontalStretchPattern.find(record) ?: return null;
            val result = mutableListOf<ClayPosition>()

            for (i in Integer.valueOf(match.groupValues[2])..Integer.valueOf(match.groupValues[3])) {
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
        val verticalSpan = Math.abs(maxX) - Math.abs(minX)

        area = Array(maxY) { y ->
            Array(verticalSpan) { x -> Tile.Sand }
        }

        leftPadding = Math.abs(minX)

        for (clay in clayPositions) {
            area[indexY(clay.y)][indexX(clay.x)] = Tile.Clay
        }

        area[indexY(0)][indexX(500)] = Tile.WaterFlowing
    }

    fun print() {
        for (y in 0 until area.size) {
            for (x in 0 until area[y].size) {
                if (area[y][x] == Tile.Sand) {
                    print(".")
                } else if (area[y][x] == Tile.Clay) {
                    print("#")
                } else if (area[y][x] == Tile.WaterSource) {
                    print("~")
                } else if (area[y][x] == Tile.WaterFlowing) {
                    print("|")
                }
            }

            println()
        }

        println()
    }

    fun flowUntilComplete() {
        while (true) {
            val updates = update()
            if (updates == 0) {
                break
            }

            //print()
        }
    }

    fun countWaterTiles(): Int {
        var result = 0

        for (y in 0 until area.size) {
            for (x in 0 until area[y].size) {
                if (area[y][x] == Tile.WaterSource || area[y][x] == Tile.WaterFlowing) {
                    result++
                }
            }
        }

        return result - 1
    }

    private fun update(): Int {
        var updates = 0

        for (y in 0 until area.size) {
            for (x in 0 until area[y].size) {
                if (area[y][x] == Tile.WaterSource || area[y][x] == Tile.WaterFlowing) {
                    if (waterFlow(x, y)) {
                        updates++
                    }
                }
            }
        }
        return updates
    }

    private fun waterFlow(x: Int, y: Int): Boolean {
        var flow = flowDown(x, y)
        if (area[y][x] == Tile.WaterFlowing) {
            flow = fillContainer(x, y) || flow
        }
        return flow
    }

    private fun flowDown(x: Int, y: Int): Boolean {
        var flow = false

        if (y + 1 < area.size) {
            if (area[y][x] == Tile.WaterFlowing && area[y + 1][x] == Tile.Clay) {
                area[y][x] = Tile.WaterSource
                flow = true
            }

            if (area[y + 1][x] == Tile.Sand) {
                area[y + 1][x] = Tile.WaterFlowing
                flow = true
            } else if (area[y][x] == Tile.WaterSource) {
                if (x - 1 >= 0 && !area[y][x - 1].blocking && area[y][x - 1] != Tile.WaterFlowing) {
                    area[y][x - 1] = Tile.WaterFlowing
                    flow = true
                }

                if (x + 1 < area.size && !area[y][x + 1].blocking && area[y][x + 1] != Tile.WaterFlowing) {
                    area[y][x + 1] = Tile.WaterFlowing
                    flow = true
                }
            }
        }

        return flow
    }

    private fun fillContainer(x: Int, y: Int): Boolean {
        var containedLeft = false
        var containedRight = false

        if (y + 1 >= area.size) {
            return false
        }

        for (i in x downTo 0) {
            if (area[y + 1][i] == Tile.Sand || area[y + 1][i] == Tile.WaterFlowing) {
                break;
            }

            if (area[y + 1][i] == Tile.Clay) {
                containedLeft = true;
                break;
            }
        }

        for (i in x until area[0].size) {
            if (area[y + 1][i] == Tile.Sand || area[y + 1][i] == Tile.WaterFlowing) {
                break;
            }

            if (area[y + 1][i] == Tile.Clay) {
                containedRight = true;
                break;
            }
        }

        if (containedLeft && containedRight) {
            area[y][x] = Tile.WaterSource
            return true
        }

        return false
    }

    private fun indexX(index: Int) = index - leftPadding
    private fun indexY(index: Int) = index

    enum class Tile(val blocking: Boolean) {
        Sand(false),
        Clay(true),
        WaterSource(true),
        WaterFlowing(false)
    }
}

fun main(args: Array<String>) {
    val source = File(object {}.javaClass.getResource("/task17/input.txt").file);
    val clayPositions: List<ClayPosition> = source.readLines()
        .flatMap { ClayPosition.parseRecord(it) }
    val map = Map(clayPositions)

    map.flowUntilComplete()

    println("Subtask #1: ${map.countWaterTiles()}")
}
