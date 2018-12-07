package com.gitlab.mkorman.aoc2018.task07

import java.io.File

data class Instruction(
    val dependency: String,
    val action: String
) {
    companion object {
        private val InstructionPattern = Regex("^Step (.*) must be finished before step (.*) can begin\\.$")

        fun parse(instruction: String): Instruction {
            val match = InstructionPattern.find(instruction)
                ?: throw IllegalArgumentException("Invalid instruction '${instruction}'")

            return Instruction(
                dependency = match.groupValues[1],
                action = match.groupValues[2]
            )
        }
    }
}

data class Step(
    val name: String,
    val dependencies: List<String>
) {
    companion object {
        fun group(instructions: List<Instruction>): List<Step> {
            val result = mutableMapOf<String, MutableList<String>>()

            for (instruction in instructions) {
                if (!result.containsKey(instruction.action)) {
                    result[instruction.action] = mutableListOf()
                }

                result[instruction.action]!!.add(instruction.dependency)
            }

            val allDependencies = result.flatMap { it.value }
            for (dependency in allDependencies) {
                if (!result.containsKey(dependency)) {
                    result[dependency] = mutableListOf()
                }
            }

            return result
                .map { (name, dependencies) -> Step(name, dependencies) }
                .sortedBy { it.name }
        }
    }
}

fun resolveOrder(steps: List<Step>): List<Step> {
    var result = mutableListOf<Step>()
    val stepsLeft = steps.toMutableList()

    while (!stepsLeft.isEmpty()) {
        for (step in stepsLeft) {
            if (dependenciesResolved(step.dependencies, result)) {
                result.add(step)
                stepsLeft.remove(step)
                break
            }
        }
    }

    return result
}

private fun dependenciesResolved(dependencies: List<String>, steps: List<Step>): Boolean {
    return dependencies.all { dep ->
        steps.find { it.name == dep } != null
    }
}

class TasksScheduler(val maxWorkers: Int) {
    val TaskTime = 60
    private val workers = mutableListOf<Task>()

    fun run(steps: List<Step>): Int {
        var time = 0
        val stepsPending = steps.toMutableList()
        val stepsDone = mutableListOf<Step>()

        while (true) {
            val tasksDone = executePending()
            tasksDone.forEach { stepsDone.add(it.step) }

            if (stepsDone.size == steps.size) {
                break
            }

            val scheduledSteps = mutableListOf<Step>()
            for (step in stepsPending) {
                if (hasFreeWorkers() && canScheduleNow(step, stepsDone)) {
                    scheduleTask(step)
                    scheduledSteps.add(step)
                }
            }
            stepsPending.removeAll(scheduledSteps)

            time++
        }

        return time
    }

    private fun executePending(): List<Task> {
        for (i in 0 until workers.size) {
            workers[i].timeLeft--
        }

        val stepsDone = workers.filter { it.timeLeft <= 0 }
        stepsDone.forEach { workers.remove(it) }

        return stepsDone
    }

    private fun hasFreeWorkers(): Boolean = workers.size < maxWorkers

    private fun canScheduleNow(step: Step, stepsDone: List<Step>): Boolean =
        step.dependencies.all { dep ->
            stepsDone.find { it.name == dep } != null
        }

    private fun scheduleTask(step: Step) {
        val stepTime = TaskTime + (step.name[0].toInt() - 'A'.toInt() + 1)
        workers.add(Task(step, stepTime))
    }

    data class Task(
        val step: Step,
        var timeLeft: Int
    )
}

fun main(args: Array<String>) {
    val source = object {}.javaClass.getResource("/task07/input.txt").file
    val instructions = File(source)
        .readLines()
        .map { Instruction.parse(it) }

    val steps = Step.group(instructions)
    val stepsInOrder = resolveOrder(steps)
    val tasksScheduler = TasksScheduler(5)

    println("Subtask #1: ${stepsInOrder.map { it.name }.joinToString("")}")
    println("Subtask #2: ${tasksScheduler.run(stepsInOrder)}")
}
