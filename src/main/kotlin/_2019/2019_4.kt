package _2019

import assertAndReturn
import measure
import readFile

private fun part1(input: String): Int {
    val (low, high) = input.split("-")
        .let { Pair(it[0].toInt(), it[1].toInt()) }

    require(low <= high)

    return (low..high).count { i ->
        val digitList = i.toString().toList()

        val isIncreasing = digitList.sorted() == digitList
        val hasDouble = digitList.zip(digitList.drop(1)).any { (a, b) -> a == b }

        isIncreasing && hasDouble
    }
}

private fun part2(input: String): Int {
    val (low, high) = input.split("-")
        .let { Pair(it[0].toInt(), it[1].toInt()) }

    require(low <= high)

    return (low..high).count { i ->
        val digitList = i.toString().toList()

        val isIncreasing = digitList.sorted() == digitList

        val hasDouble = digitList.withIndex().any { (index, value) ->
            val before = digitList.getOrNull(index - 1)
            val next = digitList.getOrNull(index + 1)
            val nextNext = digitList.getOrNull(index + 2)

            value != before && value == next && value != nextNext
        }

        isIncreasing && hasDouble
    }
}

fun main() {
    val input = readFile("/2019_4.txt")
    measure("Part 1") { assertAndReturn(part1(input), 910) }
    measure("Part 2") { assertAndReturn(part2(input), 598) }
}

