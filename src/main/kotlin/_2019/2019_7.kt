package org.example._2019

import Intcode
import org.example.readFile
import org.example.test
import permutations

private fun runAmplifiers(
    intcode: Intcode,
    state: String,
    input: List<Int>
): Int {
    require(input.size == 5)
    val a = input[0]
    val b = input[1]
    val c = input[2]
    val d = input[3]
    val e = input[4]

    val outputA = intcode.run(state, input = listOf(a, 0)).output
    require(outputA.size == 1)
    val outputB = intcode.run(state, input = listOf(b, outputA.first())).output
    require(outputB.size == 1)
    val outputC = intcode.run(state, input = listOf(c, outputB.first())).output
    require(outputC.size == 1)
    val outputD = intcode.run(state, input = listOf(d, outputC.first())).output
    require(outputD.size == 1)
    val outputE = intcode.run(state, input = listOf(e, outputD.first())).output
    require(outputE.size == 1)

    return outputE.first()
}


private fun part1(intcode: Intcode, state: String): Int {
    var result = Int.MIN_VALUE

    setOf(0, 1, 2, 3, 4).permutations().forEach { input ->
        val newResult = runAmplifiers(intcode, state, input = input)
        if (newResult > result) {
            result = newResult
        }
    }

    return result
}

private fun part2(intcode: Intcode, state: String): Int {
    var result = Int.MIN_VALUE

    setOf(0, 1, 2, 3, 4).permutations().forEach { input ->
        val newResult = runAmplifiers(intcode, state, input = input)
        if (newResult > result) {
            result = newResult
        }
    }

    return result
}

fun day7Tests(intcode: Intcode) {
    // Max thruster signal 43210 (from phase setting sequence 4,3,2,1,0)
    test(
        "Max thruster signal 43210",
        {
            part1(
                intcode = intcode,
                state = "3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0",
            )
        },
        expected = 43210
    )

    test(
        "Max thruster signal 54321",
        {
            part1(
                intcode = intcode,
                state = "3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0",
            )
        },
        expected = 54321
    )

    test(
        "Max thruster signal 65210",
        {
            part1(
                intcode = intcode,
                state = "3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0",
            )
        },
        expected = 65210
    )

    test(
        "Part 1",
        { part1(intcode = intcode, state = readFile("/2019_7.txt")) },
        expected = 11828
    )

//    test(
//        "Max thruster signal 139629729",
//        {
//            part2(
//                intcode = intcode,
//                state = "3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5",
//            )
//        },
//        expected = 139629729
//    )

}

fun main() {
    day2Tests(day5Intcode)
    day5Tests(day5Intcode)
    day7Tests(day5Intcode)
}