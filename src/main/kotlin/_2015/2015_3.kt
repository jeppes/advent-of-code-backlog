package org.example._2015

import org.example.Direction.*
import org.example.Point
import org.example.assertAndReturn
import org.example.measure
import org.example.readFile

private fun part1(line: String): Int {
    var point = Point(0, 0)
    val visited = mutableSetOf(point)
    for (char in line.toList()) {
        when (char) {
            '^' -> point = point.go(Up)
            '>' -> point = point.go(Right)
            '<' -> point = point.go(Left)
            'v' -> point = point.go(Down)
            else -> error("Invalid direction $char")
        }
        visited += point
    }

    return visited.size
}

private fun part2(line: String): Int {
    var santaPoint = Point(0, 0)
    var roboSantaPoint = Point(0, 0)

    val visited = mutableSetOf(santaPoint)

    line.forEachIndexed { index, char ->
        if (index % 2 == 0) {
            when (char) {
                '^' -> santaPoint = santaPoint.go(Up)
                '>' -> santaPoint = santaPoint.go(Right)
                '<' -> santaPoint = santaPoint.go(Left)
                'v' -> santaPoint = santaPoint.go(Down)
                else -> {}
            }
            visited += santaPoint
        } else {
            when (char) {
                '^' -> roboSantaPoint = roboSantaPoint.go(Up)
                '>' -> roboSantaPoint = roboSantaPoint.go(Right)
                '<' -> roboSantaPoint = roboSantaPoint.go(Left)
                'v' -> roboSantaPoint = roboSantaPoint.go(Down)

                else -> {}
            }
            visited += roboSantaPoint
        }
    }
    return visited.size
}

fun main() {
    val input = readFile("/2015_3.txt")
    measure("Part 1") { assertAndReturn(part1(input), 2081) }
    measure("Part 2") { assertAndReturn(part2(input), 2341) }
}
