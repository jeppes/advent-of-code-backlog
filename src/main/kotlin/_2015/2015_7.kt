package _2015

import assertAndReturn
import measure
import readFile

class KeyNotFoundException : Exception()

private fun <V> Map<String, V>.getOrThrow(key: String): V {
    require(key.toUIntOrNull() == null)
    val result = get(key) ?: throw KeyNotFoundException()
    return result
}

private fun part1(input: String): Int {
    val result = mutableMapOf<String, UShort>()
    val lines = input.lines().toMutableList()

    while (lines.isNotEmpty()) {
        val line = lines.removeFirst()
        val parts = line.split("->").map { it.trim() }
        val (left, right) = parts

        try {
            if (left.contains("AND")) {
                val andParts = left.split("AND").map { it.trim() }.filter { it.isNotEmpty() }
                val (x, y) = andParts

                val a = x.toUShortOrNull() ?: result.getOrThrow(x)
                val b = y.toUShortOrNull() ?: result.getOrThrow(y)

                result[right] = a and b
            } else if (left.contains("OR")) {
                val orParts = left.split("OR").map { it.trim() }.filter { it.isNotEmpty() }
                val (x, y) = orParts
                val a = x.toUShortOrNull() ?: result.getOrThrow(x)
                val b = y.toUShortOrNull() ?: result.getOrThrow(y)

                result[right] = a or b
            } else if (left.contains("LSHIFT")) {
                val lShiftParts = left.split("LSHIFT").map { it.trim() }.filter { it.isNotEmpty() }
                val (x, y) = lShiftParts
                val a = x.toUShortOrNull() ?: result.getOrThrow(x)
                val b = y.toUShortOrNull() ?: result.getOrThrow(y)

                result[right] = a.toInt().shl(b.toInt()).toUShort()
            } else if (left.contains("RSHIFT")) {
                val lShiftParts = left.split("RSHIFT").map { it.trim() }.filter { it.isNotEmpty() }
                val (x, y) = lShiftParts
                val a = x.toUShortOrNull() ?: result.getOrThrow(x)
                val b = y.toUShortOrNull() ?: result.getOrThrow(y)

                result[right] = a.toInt().shr(b.toInt()).toUShort()
            } else if (left.contains("NOT")) {
                val notParts = left.split("NOT").map { it.trim() }.filter { it.isNotEmpty() }
                val (x) = notParts
                val a = x.toUShortOrNull() ?: result.getOrThrow(x)
                result[right] = a.inv()
            } else {
                val leftTrim = left.trim()
                val a = leftTrim.toUShortOrNull() ?: result.getOrThrow(leftTrim)
                result[right] = a
            }
        } catch (e: KeyNotFoundException) {
            lines += line
        }
    }

    return result.getOrThrow("a").toInt()
}

private fun part2(input: String): Int {
    return part1(input.replace("1674 -> b", "46065 -> b"))
}

fun main() {
    measure("Part 1") { assertAndReturn(part1(readFile("2015_7.txt")), 46065) }
    measure("Part 2") { assertAndReturn(part2(readFile("2015_7.txt")), 14134) }
}
