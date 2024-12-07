package org.example._2019

import org.example.*
import org.example.Direction.*
import kotlin.math.abs
import kotlin.math.min

private fun letterToDirection(letter: Char): Direction {
    return when (letter) {
        'U' -> Up
        'R' -> Right
        'L' -> Left
        'D' -> Down
        else -> error("Invalid direction $letter")
    }
}


private fun part1(input: String): Int {
    val visited1 = mutableSetOf<Point>()
    var minDistance = Int.MAX_VALUE
    for ((index, wire) in input.split("\n").withIndex()) {
        require(index < 2) { "Only two wires possible" }
        var point = Point(row = 0, column = 0)
        for (step in wire.split(",")) {
            val direction = step[0]
            val count = step.drop(1).toInt()
            for (i in 0..<count) {
                point = point.go(letterToDirection(direction))
                if (index == 0) {
                    visited1.add(point)
                } else if (visited1.contains(point)) {
                    minDistance = min(minDistance, abs(point.row) + abs(point.column))
                }
            }
        }

    }

    return minDistance
}


private fun part2(input: String): Int {
    val visited1 = mutableMapOf<Point, Int>()
    var result: Int = Int.MAX_VALUE

    for ((index, wire) in input.split("\n").withIndex()) {
        require(index < 2) { "Only two wires possible" }
        var point = Point(row = 0, column = 0)
        var steps = 0
        for (step in wire.split(",")) {
            val direction = step[0]
            val count = step.drop(1).toInt()

            for (i in 0..<count) {
                steps++
                point = point.go(letterToDirection(direction))

                if (index == 0) {
                    visited1[point] = min(visited1[point] ?: Int.MAX_VALUE, steps)
                } else if (visited1.contains(point)) {
                    val stepsWire1 = visited1[point]
                    require(stepsWire1 != null)
                    val stepsWire2 = steps

                    result = min(stepsWire1 + stepsWire2, result)
                }
            }
        }
    }

    require(result != Int.MAX_VALUE)
    return result
}

fun main() {
    val input = readFile("/2019_3.txt")
    measure("Part 1") { assertAndReturn(part1(input), 245) }
    measure("Part 2") { assertAndReturn(part2(input), 48262) }
}

