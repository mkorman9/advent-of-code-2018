package com.gitlab.mkorman.aoc2018.task08

import java.io.File

data class Node(
    val child: MutableList<Node> = mutableListOf(),
    val metadata: MutableList<Int> = mutableListOf()
) {
    companion object {
        fun fromInput(input: List<Int>): Node {
            val iterator = input.iterator()
            return parseNode(iterator)
        }

        private fun parseNode(input: Iterator<Int>): Node {
            val childNum = input.next()
            val metadataNum = input.next()

            val result = Node()

            for (i in 0 until childNum) {
                result.child.add(parseNode(input))
            }

            for (i in 0 until metadataNum) {
                result.metadata.add(input.next())
            }

            return result
        }
    }

    fun getMetadataSum(): Int {
        var sum = metadata.sum()

        for (node in child) {
            sum += node.getMetadataSum()
        }

        return sum
    }

    fun getValue(): Int {
        if (child.isEmpty()) {
            return metadata.sum()
        }

        var value = 0

        for (meta in metadata) {
            val index = meta - 1
            if (index < 0 || index >= child.size) {
                continue
            }
            
            value += child.get(index).getValue()
        }

        return value
    }
}

fun main(args: Array<String>) {
    val source = object {}.javaClass.getResource("/task08/input.txt").file
    val input = File(source)
        .readText()
        .split(" ")
        .map { Integer.valueOf(it) }

    val tree = Node.fromInput(input)

    println("Subtask #1: ${tree.getMetadataSum()}")
    println("Subtask #2: ${tree.getValue()}")
}
