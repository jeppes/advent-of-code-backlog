package org.example._2019

import Continuation
import Intcode
import IntcodeState
import org.example.*
import parseIntcodeRegisters

private val day2Intcode = object : Intcode {
    override fun step(state: IntcodeState, input: Int?): Continuation {
        val mutableRegisters = state.registers.toMutableList()

        var instructionPointer = 0
        while (mutableRegisters[instructionPointer] != 99) {
            when (val instruction = mutableRegisters[instructionPointer]) {
                1,
                2 -> {
                    val register1 = mutableRegisters[instructionPointer + 1]
                    val register2 = mutableRegisters[instructionPointer + 2]
                    val register3 = mutableRegisters[instructionPointer + 3]

                    mutableRegisters[register3] =
                        if (instruction == 1) mutableRegisters[register1] + mutableRegisters[register2]
                        else mutableRegisters[register1] * mutableRegisters[register2]
                }

                else -> error("Invalid op code $instruction")
            }

            instructionPointer += 4
        }

        return Continuation.Halt(
            state = IntcodeState(
                instructionPointer = instructionPointer,
                registers = mutableRegisters,
                output = state.output,
            )
        )
    }
}

private fun part1(input: String, noun: Int = 12, verb: Int = 2): Int {
    val state = parseIntcodeRegisters(input).toMutableList()
    state[1] = noun
    state[2] = verb

    return day2Intcode.run(state, input = emptyList()).registers[0]
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

fun day2Tests(
    intcode: Intcode
) {
    // Part 1
    run {
        val input = readFile("/2019_2.txt")
        val state = parseIntcodeRegisters(input).toMutableList()
        state[1] = 12
        state[2] = 2

        test(
            "Day 2 Part 1",
            { intcode.run(registers = state, input = emptyList()).registers[0] },
            expected = 6730673
        )
    }

    // Part 2
    run {
        val input = readFile("/2019_2.txt")
        val state = parseIntcodeRegisters(input).toMutableList()
        state[1] = 37
        state[2] = 49

        test(
            "Day 2 Part 2",
            { intcode.run(registers = state, input = emptyList()).registers[0] },
            expected = 19690720
        )
    }
}


fun main() {
    val input = readFile("/2019_2.txt")
    day2Tests(day2Intcode)
    printTestSummary()

    measure("Part 2") { assertAndReturn(part2(input), 3749) }
}

