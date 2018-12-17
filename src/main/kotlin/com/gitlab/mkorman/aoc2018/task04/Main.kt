package com.gitlab.mkorman.aoc2018.task04

import java.io.File
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class TimePeriod(val begin: LocalDateTime, val end: LocalDateTime) {
    val length: Long
        get() = begin.until(end, ChronoUnit.MINUTES)
}

data class MinuteAsleep(val minute: Int, val timesAsleep: Int)

data class Guard(val id: Int, val asleepPeriods: List<TimePeriod>) {
    val sleepTime: Long
        get() = asleepPeriods.map { it.length }.sum()

    fun calculateMostSleepyMinute(): MinuteAsleep {
        val minutesAsleepCount = mutableMapOf<Int, Int>()

        if (asleepPeriods.isEmpty()) {
            return MinuteAsleep(0, 0)
        }

        for (sleep in asleepPeriods) {
            var currentTime = sleep.begin

            while (currentTime != sleep.end) {
                if (!minutesAsleepCount.containsKey(currentTime.minute)) {
                    minutesAsleepCount[currentTime.minute] = 0
                }
                minutesAsleepCount[currentTime.minute] = minutesAsleepCount[currentTime.minute]!! + 1

                currentTime = currentTime.plusMinutes(1)
            }
        }

        val mostSleepyMinute = minutesAsleepCount.maxBy { it.value }!!
        return MinuteAsleep(mostSleepyMinute.key, mostSleepyMinute.value)
    }
}

fun computeGuardsStatistics(logs: List<Log>): List<Guard> {
    var currentGuardId: Int? = null
    var sleepBeginTime: LocalDateTime? = null
    val guardsSleepPeriods = mutableMapOf<Int, MutableList<TimePeriod>>()

    for (log in logs) {
        when (log.action) {
            is BeginShiftAction -> {
                currentGuardId = log.action.guardId
                if (!guardsSleepPeriods.containsKey(currentGuardId)) {
                    guardsSleepPeriods[currentGuardId] = mutableListOf()
                }
            }
            is FallAsleepAction -> {
                sleepBeginTime = log.time
            }
            is WakeUpAction -> {
                if (sleepBeginTime == null) throw IllegalStateException("Trying to wake up but not sleeping")

                val sleepPeriod = TimePeriod(sleepBeginTime, log.time)
                guardsSleepPeriods[currentGuardId]!!.add(sleepPeriod)
                sleepBeginTime = null
            }
        }
    }

    return guardsSleepPeriods
        .map { (id, periods) -> Guard(id, periods.toList()) }
}

fun main(args: Array<String>) {
    val source = object {}.javaClass.getResource("/task04/input.txt").file
    val logs = File(source)
        .readLines()
        .map { parseLog(it) }
        .sortedBy { it.time }

    val guards = computeGuardsStatistics(logs)
    val mostSleepingGuard = guards.maxBy { it.sleepTime }!!
    val guardMostSleepingInTheSameMinute = guards.maxBy { it.calculateMostSleepyMinute().timesAsleep }!!

    println("Subtask #1: ${mostSleepingGuard.id * mostSleepingGuard.calculateMostSleepyMinute().minute}")
    println("Subtask #2: ${guardMostSleepingInTheSameMinute.id * guardMostSleepingInTheSameMinute.calculateMostSleepyMinute().minute}")
}
