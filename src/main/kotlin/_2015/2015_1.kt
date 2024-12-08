package _2015

import assertAndReturn
import measure
import readFile

private fun part1(): Int {
    val input = readFile("/2015_1.txt")

    var count = 0
    for (char in input.toList()) {
        require(char == '(' || char == ')')
        if (char == '(') {
            count++
        } else {
            count--
        }
    }

    return count
}

private fun part2(): Int {
    val input = readFile("/2015_1.txt")

    var count = 0
    for ((index, char) in input.withIndex()) {
        require(char == '(' || char == ')')
        if (char == '(') {
            count++
        } else {
            count--
        }
        if (count == -1) {
            return index + 1
        }
    }

    return count
}

fun main() {
    measure("Part 1") { assertAndReturn(part1(), 74) }
    measure("Part 2") { assertAndReturn(part2(), 1795) }
}
