package org.example._2019

import org.example._2019.Instruction.*
import org.example.assertAndReturn
import org.example.measure
import org.example.readFile
import org.example.test

data class Result(
    val state: List<Int>,
    val output: List<Int>,
)

enum class Mode {
    Immediate,
    Position,
}

sealed class Instruction {
    data class Multiplication(
        val mode1: Mode,
        val mode2: Mode,
    ) : Instruction()

    data class Addition(
        val mode1: Mode,
        val mode2: Mode,
    ) : Instruction()

    data class JumpIfTrue(
        val mode1: Mode,
        val mode2: Mode
    ) : Instruction()

    data class JumpIfFalse(
        val mode1: Mode,
        val mode2: Mode
    ) : Instruction()

    data class LessThan(
        val mode1: Mode,
        val mode2: Mode,
    ) : Instruction()

    data class Equals(
        val mode1: Mode,
        val mode2: Mode,
    ) : Instruction()

    data object ReadInput : Instruction()

    data class WriteOutput(val mode1: Mode) : Instruction()

    data object Halt : Instruction()
}

fun Int.toMode(): Mode {
    return when (this) {
        0 -> Mode.Position
        1 -> Mode.Immediate
        else -> error("Unexpected mode $this")
    }
}

private fun parseInstruction(
    instruction: Int
): Instruction {
    val reversed = instruction.toString().toList().reversed()

    val opCode = reversed.take(2).joinToString("").reversed().toInt()
    val mode1 = (reversed.getOrNull(2))?.toString()?.toInt()?.toMode()
    val mode2 = (reversed.getOrNull(3))?.toString()?.toInt()?.toMode()
    val mode3 = (reversed.getOrNull(4))?.toString()?.toInt()?.toMode()

    return when (opCode) {
        1 -> {
            require(mode3 == Mode.Position || mode3 == null) { "Got $mode3, instruction $instruction" }
            Addition(
                mode1 = mode1 ?: Mode.Position,
                mode2 = mode2 ?: Mode.Position,
            )
        }

        2 -> {
            require(mode3 == Mode.Position || mode3 == null) { "Got $mode3, instruction $instruction" }
            Multiplication(
                mode1 = mode1 ?: Mode.Position,
                mode2 = mode2 ?: Mode.Position,
            )
        }

        3 -> {
            require(mode1 == Mode.Position || mode1 == null) { "Got $mode1, instruction $instruction" }
            require(mode2 == null) { "Got $mode2, instruction $instruction" }
            require(mode3 == null) { "Got $mode3, instruction $instruction" }

            ReadInput
        }

        4 -> {
            require(mode2 == null) { "Got $mode2, instruction $instruction" }
            require(mode3 == null) { "Got $mode3, instruction $instruction" }

            WriteOutput(mode1 = mode1 ?: Mode.Position)
        }

        5 -> {
            require(mode3 == null) { "Got $mode3, instruction $instruction" }
            JumpIfTrue(mode1 = mode1 ?: Mode.Position, mode2 = mode2 ?: Mode.Position)
        }

        6 -> {
            require(mode3 == null) { "Got $mode3, instruction $instruction" }
            JumpIfFalse(mode1 = mode1 ?: Mode.Position, mode2 = mode2 ?: Mode.Position)
        }

        7 -> {
            require(mode3 == null) { "Got $mode3, instruction $instruction" }
            LessThan(
                mode1 = mode1 ?: Mode.Position,
                mode2 = mode2 ?: Mode.Position,
            )
        }

        8 -> {
            require(mode3 == null) { "Got $mode3, instruction $instruction" }
            Equals(
                mode1 = mode1 ?: Mode.Position,
                mode2 = mode2 ?: Mode.Position,
            )
        }

        99 -> {
            require(mode1 == null) { "Got $mode1, instruction $instruction" }
            require(mode2 == null) { "Got $mode2, instruction $instruction" }
            require(mode3 == null) { "Got $mode3, instruction $instruction" }

            Halt
        }

        else -> {
            error("Unable to parse instruction $instruction")
        }
    }
}


fun interpretParameter(state: List<Int>, mode: Mode, parameter: Int): Int {
    return when (mode) {
        Mode.Immediate -> parameter
        Mode.Position -> state[parameter]
    }
}

private fun intcode(startStateString: String, input: Int?): Result {
    return intcode(parseInput(startStateString), input = input)
}

private fun intcode(startState: List<Int>, input: Int?): Result {
    val state = startState.toMutableList()
    var instructionsExecuted = 0
    val output = mutableListOf<Int>()
    var hasReadInput = false
    var instructionPointer = 0
    var previousInstructionPointer: Int? = null
    var latestInstruction: Instruction? = null

    try {
        while (true) {
            require(previousInstructionPointer != instructionPointer) {
                "Instruction pointer should change"
            }
            previousInstructionPointer = instructionPointer

            require(instructionsExecuted < 10_000_000) {
                "Runtime exceeded"
            }
            instructionsExecuted++

            require(state.any { it == 99 }) {
                "HALT instruction overwritten"
            }

            val instruction =
                parseInstruction(state[instructionPointer])
            latestInstruction = instruction

            when (instruction) {
                is Addition -> {
                    val parameter1 = state[instructionPointer + 1]
                    val parameter2 = state[instructionPointer + 2]
                    val parameter3 = state[instructionPointer + 3]
                    val value1 = interpretParameter(state, instruction.mode1, parameter1)
                    val value2 = interpretParameter(state, instruction.mode2, parameter2)

                    state[parameter3] = value1 + value2


                    instructionPointer += 4
                }

                is Multiplication -> {
                    val parameter1 = state[instructionPointer + 1]
                    val parameter2 = state[instructionPointer + 2]
                    val parameter3 = state[instructionPointer + 3]
                    val value1 = interpretParameter(state, instruction.mode1, parameter1)
                    val value2 = interpretParameter(state, instruction.mode2, parameter2)

                    state[parameter3] = value1 * value2

                    instructionPointer += 4
                }

                ReadInput -> {
                    val parameter1 = state[instructionPointer + 1]
                    require(!hasReadInput)
                    require(input != null)
                    state[parameter1] = input
                    hasReadInput = true

                    instructionPointer += 2
                }

                is WriteOutput -> {
                    val parameter1 = state[instructionPointer + 1]
                    val value = interpretParameter(state, instruction.mode1, parameter1)
                    output += value

                    instructionPointer += 2
                }

                is JumpIfTrue -> {
                    val parameter1 = state[instructionPointer + 1]
                    val parameter2 = state[instructionPointer + 2]
                    val value1 = interpretParameter(state, instruction.mode1, parameter1)
                    val value2 = interpretParameter(state, instruction.mode2, parameter2)


                    if (value1 != 0) {
                        instructionPointer = value2
                    } else {
                        instructionPointer += 3
                    }
                }

                is JumpIfFalse -> {
                    val parameter1 = state[instructionPointer + 1]
                    val parameter2 = state[instructionPointer + 2]
                    val value1 = interpretParameter(state, instruction.mode1, parameter1)
                    val value2 = interpretParameter(state, instruction.mode2, parameter2)

                    if (value1 == 0) {
                        instructionPointer = value2
                    } else {
                        instructionPointer += 3
                    }
                }

                is LessThan -> {
                    val parameter1 = state[instructionPointer + 1]
                    val parameter2 = state[instructionPointer + 2]
                    val toPosition = state[instructionPointer + 3]
                    val value1 = interpretParameter(state, instruction.mode1, parameter1)
                    val value2 = interpretParameter(state, instruction.mode2, parameter2)

                    if (value1 < value2) {
                        state[toPosition] = 1
                    } else {
                        state[toPosition] = 0
                    }
                    instructionPointer += 4
                }

                is Equals -> {
                    val parameter1 = state[instructionPointer + 1]
                    val parameter2 = state[instructionPointer + 2]
                    val toPosition = state[instructionPointer + 3]
                    val value1 = interpretParameter(state, instruction.mode1, parameter1)
                    val value2 = interpretParameter(state, instruction.mode2, parameter2)

                    if (value1 == value2) {
                        state[toPosition] = 1
                    } else {
                        state[toPosition] = 0
                    }
                    instructionPointer += 4
                }

                Halt -> {
                    return Result(state = state, output = output)
                }
            }
        }
    } catch (e: Exception) {
        println("\uD83D\uDCA5\uD83D\uDCA5\uD83D\uDCA5")
        println("Crash state: $state")
        println("Start state: $startState")
        println("Input: $input")
        println("Output: $output")
        println("Instructions executed: $instructionsExecuted")
        println("Instruction pointer: $instructionPointer")
        println("Latest instruction: $latestInstruction")
        println("Has read input: $hasReadInput")
        throw e
    }
}

private fun parseInput(input: String): List<Int> {
    return input.split(",").map { it.toInt() }
}

fun main() {
    run {
        // Test from day 2
        val input = readFile("/2019_2.txt")
        val state = parseInput(input).toMutableList()
        state[1] = 12
        state[2] = 2

        test(
            "Multiplication and addition from day 2",
            { intcode(startState = state, input = null).state[0] },
            expected = 6730673
        )
    }

    run {
        // Examples in part 1

        // The program 3,0,4,0,99 outputs whatever it gets as input, then halts.
        val example = "3,0,4,0,99"
        test(
            "Prints input 5",
            { intcode(parseInput(example), input = 5).output },
            expected = listOf(5)
        )
        test(
            "Prints the input 15",
            { intcode(parseInput(example), input = 15).output },
            expected = listOf(15)
        )
    }

    val input = readFile("/2019_5.txt")
    val state = parseInput(input)
    measure("Part 1") {
        val outputs = intcode(startState = state, input = 1).output
        for (i in outputs.dropLast(1)) {
            assertAndReturn(i, 0)
        }
        assertAndReturn(outputs.last(), 15508323)
    }


    run {
        // Examples in part 2

        // 3,9,8,9,10,9,4,9,99,-1,8 - Using position mode, consider whether the input is
        // equal to 8; output 1 (if it is) or 0 (if it is not).
        test(
            "Equal - Position mode, input 8 -> 1",
            { intcode("3,9,8,9,10,9,4,9,99,-1,8", input = 8).output.last() },

            expected = 1
        )
        test(
            "Equal - Position mode input != 8 -> 0",
            { intcode("3,9,8,9,10,9,4,9,99,-1,8", input = 4).output.last() },
            expected = 0
        )

        // 3,9,7,9,10,9,4,9,99,-1,8 - Using position mode, consider whether the input is
        // less than 8; output 1 (if it is) or 0 (if it is not).
        test(
            "Less Than - Position mode, input < 8 -> 1",
            { intcode("3,9,7,9,10,9,4,9,99,-1,8", input = 7).output },
            expected = listOf(1)
        )
        test(
            "Less Than - Position mode, input = 8 -> 0",
            { intcode("3,9,7,9,10,9,4,9,99,-1,8", input = 8).output },
            expected = listOf(0)
        )
        test(
            "Less Than - Position mode, input = 9 -> 0",
            { intcode("3,9,7,9,10,9,4,9,99,-1,8", input = 9).output },
            expected = listOf(0)
        )

        // 3,3,1108,-1,8,3,4,3,99 - Using immediate mode, consider whether the input is
        // equal to 8; output 1 (if it is) or 0 (if it is not).
        test(
            "Equal - Immediate mode, input = 8 -> 1",
            { intcode("3,3,1108,-1,8,3,4,3,99", input = 8).output },
            expected = listOf(1)
        )
        test(
            "Equal - Immediate mode, input = 7 -> 0",
            { intcode("3,3,1108,-1,8,3,4,3,99", input = 7).output },
            expected = listOf(0)
        )

        // 3,3,1107,-1,8,3,4,3,99 - Using immediate mode, consider whether the input is
        // less than 8; output 1 (if it is) or 0 (if it is not).
        test(
            "Less Than - Immediate mode, input = 7 -> 1",
            { intcode("3,3,1107,-1,8,3,4,3,99", input = 7).output },
            expected = listOf(1)
        )
        test(
            "Less Than - Immediate mode, input = 8 -> 0",
            { intcode("3,3,1107,-1,8,3,4,3,99", input = 8).output },
            expected = listOf(0)
        )

        test(
            "Less Than - Immediate mode, input = 8 -> 0",
            { intcode("3,3,1107,-1,8,3,4,3,99", input = 9).output },
            expected = listOf(0)
        )

        // Here are some jump tests that take an input, then output 0 if the input
        // was zero or 1 if the input was non-zero:
        //  3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9 (using position mode)
        //  3,3,1105,-1,9,1101,0,0,12,4,12,99,1 (using immediate mode)
        test(
            "Jump - Position mode, input = 0 -> 0",
            { intcode("3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9", input = 0).output },
            expected = listOf(0)
        )
        test(
            "Jump - Position mode, input = 1 -> 1",
            { intcode("3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9", input = 1).output },
            expected = listOf(1)
        )
        test(
            "Jump - Position mode, input = -1 -> 1",
            { intcode("3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9", input = -1).output },
            expected = listOf(1)
        )
        test(
            "Jump - Immediate mode, input = 0 -> 0",
            { intcode("3,3,1105,-1,9,1101,0,0,12,4,12,99,1", input = 0).output },
            expected = listOf(0)
        )
        test(
            "Jump - Immediate mode, input = 1 -> 1",
            { intcode("3,3,1105,-1,9,1101,0,0,12,4,12,99,1", input = 1).output },
            expected = listOf(1)
        )
        test(
            "Jump - Immediate mode, input = -1 -> 1",
            { intcode("3,3,1105,-1,9,1101,0,0,12,4,12,99,1", input = -1).output },
            expected = listOf(1)
        )

        // 3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,
        // 1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,
        // 999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99
        //
        // The above example program uses an input instruction to ask for a
        // single number. The program will then output 999 if the input value is
        // below 8, output 1000 if the input value is equal to 8, or output 1001
        // if the input value is greater than 8.
        val longProgram =
            "3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99"
        test(
            "Long program - input = 7 -> 999",
            { intcode(longProgram, input = 7).output },
            listOf(999)
        )
        test(
            "Long program - input = 8 -> 1000",
            { intcode(longProgram, input = 8).output },
            listOf(1000)
        )
        test(
            "Long program - input = 9 -> 1001",
            { intcode(longProgram, input = 9).output },
            listOf(1001)
        )
    }

    measure("Part 2") {
        assertAndReturn(
            intcode(startState = state, input = 5).output,
            listOf(9006327)
        )
    }
}

