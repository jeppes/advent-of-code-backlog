package org.example._2019

import org.example.assertAndReturn
import org.example.measure
import org.example.readFile

private fun part1(input: String, noun: Int = 12, verb: Int = 2): Int {
    val state = input.split(",")
        .map { it.toInt() }
        .toMutableList()

    state[1] = noun
    state[2] = verb

    var instructionPointer = 0
    while (state[instructionPointer] != 99) {
        when (val instruction = state[instructionPointer]) {
            1,
            2 -> {
                val register1 = state[instructionPointer + 1]
                val register2 = state[instructionPointer + 2]
                val register3 = state[instructionPointer + 3]

                state[register3] =
                    if (instruction == 1) state[register1] + state[register2]
                    else state[register1] * state[register2]
            }

            else -> error("Invalid op code $instruction")
        }

        instructionPointer += 4
    }

    return state[0]
}

private fun part2(input: String): Int {
    for (noun in 0..99) {
        for (verb in 0..99) {
            if (part1(input = input, noun = noun, verb = verb) == 19690720) {
                return 100 * noun + verb
            }
        }
    }
    error("No valid noun+verb combinations found")
}

fun main() {
    val input = readFile("/2019_2.txt")
    measure("Part 1") { assertAndReturn(part1(input), 6730673) }
    measure("Part 2") { assertAndReturn(part2(input), 3749) }
}

