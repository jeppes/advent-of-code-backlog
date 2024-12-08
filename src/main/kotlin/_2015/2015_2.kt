package _2015

import assertAndReturn
import measure
import readFile
import kotlin.math.min

private fun parseLine(line: String): Triple<Int, Int, Int> {
    return line.split("x").let {
        Triple(
            it[0].toInt(),
            it[1].toInt(),
            it[2].toInt(),
        )
    }
}

private fun part1(): Int {
    return readFile("/2015_2.txt").lines().sumOf { line ->
        val (l, w, h) = parseLine(line)
        2 * l * w + 2 * w * h + 2 * h * l + min(l * w, min(w * h, h * l))
    }
}

private fun part2(): Int {
    return readFile("/2015_2.txt").lines().sumOf { line ->
        val (l, w, h) = parseLine(line)

        listOf(2 * l + 2 * w, 2 * w + 2 * h, 2 * h + 2 * l).min() + l * w * h
    }
}

fun main() {
    measure("Part 1") { assertAndReturn(part1(), 1588178) }
    measure("Part 2") { assertAndReturn(part2(), 3783758) }
}
