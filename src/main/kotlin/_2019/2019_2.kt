package _2019

import assertAndReturn
import measure
import printTestSummary
import readFile
import test

private fun part1(input: String, noun: Int = 12, verb: Int = 2): Int {
    val state = parseIntcodeRegisters(input).toMutableList()
    state[1] = noun
    state[2] = verb

    return mainIntcode.run(state, input = emptyList()).registers[0]
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
    day2Tests(mainIntcode)
    printTestSummary()

    measure("Part 2") { assertAndReturn(part2(input), 3749) }
}

