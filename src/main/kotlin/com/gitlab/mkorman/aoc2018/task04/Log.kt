package com.gitlab.mkorman.aoc2018.task04

import java.time.LocalDateTime

val LogPattern = Regex("^\\[(\\d+)-(\\d+)-(\\d+) (\\d+):(\\d+)] (.*)$")
val BeginShiftPattern = Regex("^Guard #(\\d+) begins shift$")

fun parseLog(logStr: String): Log {
    val match = LogPattern.find(logStr) ?: throw IllegalArgumentException("Invalid log item '${logStr}'")

    return Log(
        time = LocalDateTime.of(
            Integer.valueOf(match.groupValues[1]),  // year
            Integer.valueOf(match.groupValues[2]),  // month
            Integer.valueOf(match.groupValues[3]),  // day
            Integer.valueOf(match.groupValues[4]),  // hour
            Integer.valueOf(match.groupValues[5])   // minute
        ),
        action = parseLogAction(match.groupValues[6])
    )
}

private fun parseLogAction(logActionStr: String): Action {
    val logAction = logActionStr.trim()

    return when(logAction) {
        "falls asleep" -> FallAsleepAction()
        "wakes up" -> WakeUpAction()
        else -> {
            val match = BeginShiftPattern.find(logAction) ?: throw IllegalArgumentException("Invalid action '${logAction}'")
            return BeginShiftAction(
                guardId = Integer.valueOf(match.groupValues[1])
            )
        }
    }
}

interface Action {
}

class BeginShiftAction(val guardId: Int) : Action {
}

class FallAsleepAction : Action {

}

class WakeUpAction : Action {
}

data class Log(
    val time: LocalDateTime,
    val action: Action
)
