package com.gitlab.mkorman.aoc2018.task03

import java.io.File

data class Claim(
    val id: Int,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
)

val ClaimPattern = Regex("^#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)$")

fun parseClaim(claimStr: String): Claim {
    val match = ClaimPattern.find(claimStr) ?: throw IllegalArgumentException("Invalid claim '${claimStr}'")

    return Claim(
        id = Integer.valueOf(match.groupValues[1]),
        x = Integer.valueOf(match.groupValues[2]),
        y = Integer.valueOf(match.groupValues[3]),
        width = Integer.valueOf(match.groupValues[4]),
        height = Integer.valueOf(match.groupValues[5])
    )
}

data class FabricSquareInch(
    val claims: MutableSet<Claim> = mutableSetOf()
)

class Fabric(val width: Int, val height: Int) {
    val grid: Array<FabricSquareInch> = Array(width * height, { _ -> FabricSquareInch() })

    fun addClaim(claim: Claim) {
        for (x in claim.x until (claim.x + claim.width)) {
            for (y in claim.y until (claim.y + claim.height)) {
                grid[y * width + x].claims.add(claim)
            }
        }
    }

    fun getIntersectionArea(): Int {
        var totalArea = 0

        for (squareInch in grid) {
            if (squareInch.claims.size > 1) {
                totalArea++
            }
        }

        return totalArea
    }

    fun isIndependentClaim(claim: Claim): Boolean {
        for (x in claim.x until (claim.x + claim.width)) {
            for (y in claim.y until (claim.y + claim.height)) {
                if (grid[y * width + x].claims.size != 1) {
                    return false
                }
            }
        }

        return true
    }
}

fun main(args: Array<String>) {
    val source = object {}.javaClass.getResource("/task03/input.txt").file
    val claims = File(source)
        .readLines()
        .map { parseClaim(it) }

    val fabricWidth = claims
        .map({ it.x + it.width + 1 })
        .max()!!
    val fabricHeight = claims
        .map({ it.y + it.height + 1 })
        .max()!!
    val fabric = Fabric(fabricWidth, fabricHeight)

    claims.forEach { fabric.addClaim(it) }

    println("Subtask #1: ${fabric.getIntersectionArea()}")
    println("Subtask #2: ${claims.filter { fabric.isIndependentClaim(it) }[0].id}")
}
