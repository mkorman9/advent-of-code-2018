package com.gitlab.mkorman.aoc2018.task13

import java.io.File

enum class MapTile(val representation: Char) {
    Empty(' '),
    Vertical('|'),
    Horizontal('-'),
    ClockwiseCorner('/'),
    CounterClockwiseCorner('\\'),
    Crossing('+'),
    CartRight('>'),
    CartLeft('<'),
    CartUp('^'),
    CartDown('v');

    companion object {
        fun parse(input: Char): MapTile {
            for (tile in values()) {
                if (tile.representation == input) {
                    return tile
                }
            }

            throw IllegalArgumentException("invalid map tile '${input}'")
        }
    }
}

data class Cart(
    var x: Int,
    var y: Int,
    var direction: Direction
) {
    private var nextMoveSequenceNumber: Int = 0

    fun move(tileUnder: MapTile) {
        when (tileUnder) {
            MapTile.Horizontal -> {
                if (direction == Direction.Right) {
                    x++
                } else if (direction == Direction.Left) {
                    x--
                }
            }
            MapTile.Vertical -> {
                if (direction == Direction.Down) {
                    y++
                } else if (direction == Direction.Up) {
                    y--
                }
            }
            MapTile.ClockwiseCorner -> {
                if (direction == Direction.Up) {
                    direction = Direction.Right
                    x++
                } else if (direction == Direction.Left) {
                    direction = Direction.Down
                    y++
                } else if (direction == Direction.Down) {
                    direction = Direction.Left
                    x--
                } else if (direction == Direction.Right) {
                    direction = Direction.Up
                    y--
                }
            }
            MapTile.CounterClockwiseCorner -> {
                if (direction == Direction.Up) {
                    direction = Direction.Left
                    x--
                } else if (direction == Direction.Left) {
                    direction = Direction.Up
                    y--
                } else if (direction == Direction.Down) {
                    direction = Direction.Right
                    x++
                } else if (direction == Direction.Right) {
                    direction = Direction.Down
                    y++
                }
            }
            MapTile.Crossing -> {
                val decision = MovesSequence[nextMoveSequenceNumber]
                nextMoveSequenceNumber = (nextMoveSequenceNumber + 1) % MovesSequence.size

                if (decision == Move.Left) {
                    if (direction == Direction.Up) {
                        direction = Direction.Left
                        x--
                    } else if (direction == Direction.Left) {
                        direction = Direction.Down
                        y++
                    } else if (direction == Direction.Down) {
                        direction = Direction.Right
                        x++
                    } else if (direction == Direction.Right) {
                        direction = Direction.Up
                        y--
                    }
                } else if (decision == Move.Right) {
                    if (direction == Direction.Up) {
                        direction = Direction.Right
                        x++
                    } else if (direction == Direction.Left) {
                        direction = Direction.Up
                        y--
                    } else if (direction == Direction.Down) {
                        direction = Direction.Left
                        x--
                    } else if (direction == Direction.Right) {
                        direction = Direction.Down
                        y++
                    }
                } else {
                    if (direction == Direction.Up) {
                        y--
                    } else if (direction == Direction.Left) {
                        x--
                    } else if (direction == Direction.Down) {
                        y++
                    } else if (direction == Direction.Right) {
                        x++
                    }
                }
            }
            else -> throw IllegalStateException("cart out of track")
        }
    }

    enum class Direction {
        Left,
        Right,
        Up,
        Down
    }

    enum class Move {
        Left,
        Straight,
        Right
    }

    companion object {
        val MovesSequence = listOf(
            Move.Left,
            Move.Straight,
            Move.Right
        )
    }
}

data class CartCollision(
    val cart1: Cart,
    val cart2: Cart,
    val tick: Int
)

class Map(lines: List<String>) {
    private val carts = mutableListOf<Cart>()
    private val area: Array<Array<MapTile>> =
        Array(lines.size) { row -> Array(lines[row].length) { column -> createTile(lines[row][column], row, column) } }

    fun simulateUntilCollision(): CartCollision {
        var tickNumber = 0

        while (true) {
            val collisions = tick(
                n = tickNumber,
                stopAfterFirstCollision = true
            )

            if (!collisions.isEmpty()) {
                return collisions[0]
            }

            tickNumber++
        }
    }

    fun simulateUntilLastCartRemains(): Cart {
        var tickNumber = 0

        while (true) {
            if (carts.size == 1) {
                return carts[0]
            }

            val collisions = tick(
                n = tickNumber,
                stopAfterFirstCollision = false
            )

            for (collision in collisions) {
                carts.remove(collision.cart1)
                carts.remove(collision.cart2)
            }

            tickNumber++
        }
    }

    private fun tick(n: Int, stopAfterFirstCollision: Boolean): List<CartCollision> {
        carts.sortBy { it.y * carts.size + it.x }

        for (cart in carts) {
            cart.move(area[cart.y][cart.x])

            if (stopAfterFirstCollision) {
                val collisions = checkForCollision(n)
                if (!collisions.isEmpty()) {
                    return collisions
                }
            }
        }

        return checkForCollision(n)
    }

    private fun createTile(input: Char, row: Int, column: Int): MapTile {
        val tile = MapTile.parse(input)

        return when (tile) {
            MapTile.CartRight -> {
                addCart(row, column, Cart.Direction.Right); MapTile.Horizontal
            }
            MapTile.CartLeft -> {
                addCart(row, column, Cart.Direction.Left); MapTile.Horizontal
            }
            MapTile.CartUp -> {
                addCart(row, column, Cart.Direction.Up); MapTile.Vertical
            }
            MapTile.CartDown -> {
                addCart(row, column, Cart.Direction.Down); MapTile.Vertical
            }
            else -> tile
        }
    }

    private fun addCart(y: Int, x: Int, direction: Cart.Direction) {
        carts.add(
            Cart(
                y = y,
                x = x,
                direction = direction
            )
        )
    }

    private fun checkForCollision(n: Int): List<CartCollision> {
        val collisions = mutableListOf<Pair<Cart, Cart>>()

        for (cart1 in carts) {
            for (cart2 in carts) {
                if (cart1 != cart2) {
                    if (cart1.x == cart2.x && cart1.y == cart2.y) {
                        collisions.add(Pair(cart1, cart2))
                    }
                }
            }
        }

        return collisions.map { CartCollision(it.first, it.second, n) }
    }
}

fun main(args: Array<String>) {
    val source = File(object {}.javaClass.getResource("/task13/input.txt").file)
    val mapLines = source.readLines()

    println("Subtask #1: ${Map(mapLines).simulateUntilCollision()}")
    println("Subtask #2: ${Map(mapLines).simulateUntilLastCartRemains()}")
}
