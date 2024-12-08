package org.example._2019

import org.example.assertAndReturn
import org.example.measure
import org.example.readFile

private fun part1(input: String): Int {
    val orbits = mutableMapOf<String, List<String>>()

    for (line in input.lines()) {
        val parts = line.split(")").filter { it.isNotBlank() }

        val a = parts[0]
        val b = parts[1]
        orbits[a] = (orbits[a] ?: emptyList()) + listOf(b)
    }

    var count = 0
    val nextKeys = orbits.keys.toMutableList()
    while (nextKeys.isNotEmpty()) {
        val next = nextKeys.removeFirst()
        count++

        nextKeys.addAll(orbits[next] ?: emptyList())
    }
    return count - orbits.keys.size
}

private fun part2(input: String): Int {
    val orbiting = mutableMapOf<String, String>()

    for (line in input.lines()) {
        val parts = line.split(")").filter { it.isNotBlank() }

        val a = parts[0]
        val b = parts[1]
        require(orbiting[b] == null)
        orbiting[b] = a
    }

    fun orbitingPath(start: String): List<String> {
        val path = mutableListOf<String>()
        var next = orbiting[start]
        while (next != null) {
            path += next
            next = orbiting[next]
        }
        return path
    }

    val orbitingYou = orbitingPath("YOU")
    val orbitingSanta = orbitingPath("SAN")

    val overlap = orbitingYou.withIndex().find { (_, i) -> orbitingSanta.contains(i) }!!
    val distanceFromOverlapToSanta = orbitingSanta.indexOf(overlap.value)

    return (overlap.index) + distanceFromOverlapToSanta
}

fun main() {
    measure("Part 1") { assertAndReturn(part1(readFile("/2019_6.txt")), 147807) }
    measure("Part 2") { assertAndReturn(part2(readFile("/2019_6.txt")), 229) }
}
