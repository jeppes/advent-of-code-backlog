package org.example._2019

import org.example.assertAndReturn
import org.example.measure
import org.example.readFile
import kotlin.math.floor
import kotlin.math.max

private fun part1(input: String): Long {
    return input.lines().sumOf { line ->
        (floor(line.toDouble() / 3) - 2).toLong()
    }
}

private fun part2(input: String): Long {
    return input.lines().sumOf { line ->
        var innerSum = 0L
        var number = line.toLong()
        while (number > 0L) {
            number = max(0, (floor(number.toDouble() / 3) - 2).toLong())
            innerSum += number
        }
        innerSum
    }
}

fun main() {
    val input = readFile("/2019_1.txt")
    measure("Part 1") { assertAndReturn(part1(input), 3216868) }
    measure("Part 2") { assertAndReturn(part2(input), 4822435) }
}

