package _2019

import _2019.Instruction.*

fun parseIntcodeRegisters(input: String): List<Int> {
    return input.split(",").map { it.toInt() }
}

data class IntcodeState(
    val instructionPointer: Int,
    val registers: List<Int>,
    val output: List<Int>,
) {
    companion object {
        fun fromRegisters(registers: List<Int>): IntcodeState {
            return IntcodeState(
                instructionPointer = 0,
                registers = registers,
                output = emptyList()
            )
        }

        fun fromRegisters(registers: String): IntcodeState {
            return fromRegisters(parseIntcodeRegisters(registers))
        }
    }
}

sealed interface Continuation {
    val state: IntcodeState

    data class NeedInput(
        val next: (Int) -> Continuation,
        override val state: IntcodeState
    ) : Continuation

    data class Halt(
        override val state: IntcodeState
    ) : Continuation
}

interface Intcode {
    fun step(
        state: IntcodeState,
        input: Int?
    ): Continuation

    fun run(
        registers: List<Int>,
        input: List<Int>
    ): IntcodeState {
        val mutableInput = input.toMutableList()

        var next = this.step(
            state = IntcodeState.fromRegisters(registers),
            input = mutableInput.removeFirstOrNull()
        )

        while (next !is Continuation.Halt) {
            next = when (next) {
                is Continuation.NeedInput -> {
                    next.next(mutableInput.removeFirst())
                }

                else -> {
                    error("Illegal state $next")
                }
            }
        }

        if (mutableInput.isNotEmpty()) {
            println("⚠\uFE0F Finished with unused input: $mutableInput")
        }

        return next.state
    }

    fun run(
        registersString: String,
        input: List<Int>
    ): IntcodeState {
        return run(parseIntcodeRegisters(registersString), input = input)
    }
}

private enum class Mode {
    Immediate,
    Position,
}

private sealed class Instruction {
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

private fun Int.toMode(): Mode {
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


private fun interpretParameter(state: List<Int>, mode: Mode, parameter: Int): Int {
    return when (mode) {
        Mode.Immediate -> parameter
        Mode.Position -> state[parameter]
    }
}


val mainIntcode = object : Intcode {
    override fun step(
        state: IntcodeState,
        input: Int?
    ): Continuation {
        var mutableInput = input
        val mutableRegisters = state.registers.toMutableList()
        val output = state.output.toMutableList()
        var instructionPointer = state.instructionPointer

        try {
            while (true) {
                require(mutableRegisters.any { it == 99 }) {
                    "HALT instruction overwritten"
                }

                val instruction =
                    parseInstruction(mutableRegisters[instructionPointer])

                when (instruction) {
                    is Addition -> {
                        val parameter1 = mutableRegisters[instructionPointer + 1]
                        val parameter2 = mutableRegisters[instructionPointer + 2]
                        val parameter3 = mutableRegisters[instructionPointer + 3]
                        val value1 = interpretParameter(mutableRegisters, instruction.mode1, parameter1)
                        val value2 = interpretParameter(mutableRegisters, instruction.mode2, parameter2)

                        mutableRegisters[parameter3] = value1 + value2


                        instructionPointer += 4
                    }

                    is Multiplication -> {
                        val parameter1 = mutableRegisters[instructionPointer + 1]
                        val parameter2 = mutableRegisters[instructionPointer + 2]
                        val parameter3 = mutableRegisters[instructionPointer + 3]
                        val value1 = interpretParameter(mutableRegisters, instruction.mode1, parameter1)
                        val value2 = interpretParameter(mutableRegisters, instruction.mode2, parameter2)

                        mutableRegisters[parameter3] = value1 * value2

                        instructionPointer += 4
                    }

                    ReadInput -> {
                        val parameter1 = mutableRegisters[instructionPointer + 1]
                        if (mutableInput == null) {
                            val nextState = IntcodeState(
                                instructionPointer = instructionPointer,
                                registers = mutableRegisters,
                                output = output,
                            )

                            return Continuation.NeedInput(
                                next = { nextInput ->
                                    step(state = nextState, input = nextInput)
                                },
                                state = nextState
                            )
                        } else {
                            mutableRegisters[parameter1] = mutableInput
                            mutableInput = null
                            instructionPointer += 2
                        }
                    }

                    is WriteOutput -> {
                        val parameter1 = mutableRegisters[instructionPointer + 1]
                        val value = interpretParameter(mutableRegisters, instruction.mode1, parameter1)
                        output += value

                        instructionPointer += 2
                    }

                    is JumpIfTrue -> {
                        val parameter1 = mutableRegisters[instructionPointer + 1]
                        val parameter2 = mutableRegisters[instructionPointer + 2]
                        val value1 = interpretParameter(mutableRegisters, instruction.mode1, parameter1)
                        val value2 = interpretParameter(mutableRegisters, instruction.mode2, parameter2)


                        if (value1 != 0) {
                            instructionPointer = value2
                        } else {
                            instructionPointer += 3
                        }
                    }

                    is JumpIfFalse -> {
                        val parameter1 = mutableRegisters[instructionPointer + 1]
                        val parameter2 = mutableRegisters[instructionPointer + 2]
                        val value1 = interpretParameter(mutableRegisters, instruction.mode1, parameter1)
                        val value2 = interpretParameter(mutableRegisters, instruction.mode2, parameter2)

                        if (value1 == 0) {
                            instructionPointer = value2
                        } else {
                            instructionPointer += 3
                        }
                    }

                    is LessThan -> {
                        val parameter1 = mutableRegisters[instructionPointer + 1]
                        val parameter2 = mutableRegisters[instructionPointer + 2]
                        val toPosition = mutableRegisters[instructionPointer + 3]
                        val value1 = interpretParameter(mutableRegisters, instruction.mode1, parameter1)
                        val value2 = interpretParameter(mutableRegisters, instruction.mode2, parameter2)

                        if (value1 < value2) {
                            mutableRegisters[toPosition] = 1
                        } else {
                            mutableRegisters[toPosition] = 0
                        }
                        instructionPointer += 4
                    }

                    is Equals -> {
                        val parameter1 = mutableRegisters[instructionPointer + 1]
                        val parameter2 = mutableRegisters[instructionPointer + 2]
                        val toPosition = mutableRegisters[instructionPointer + 3]
                        val value1 = interpretParameter(mutableRegisters, instruction.mode1, parameter1)
                        val value2 = interpretParameter(mutableRegisters, instruction.mode2, parameter2)

                        if (value1 == value2) {
                            mutableRegisters[toPosition] = 1
                        } else {
                            mutableRegisters[toPosition] = 0
                        }
                        instructionPointer += 4
                    }

                    Halt -> {
                        val nextState = IntcodeState(
                            instructionPointer = instructionPointer,
                            registers = mutableRegisters,
                            output = output,
                        )

                        return Continuation.Halt(state = nextState)
                    }
                }
            }
        } catch (e: Exception) {
            println("\uD83D\uDCA5\uD83D\uDCA5\uD83D\uDCA5")
            println("Crash registers: $mutableRegisters")
            println("Start registers: ${state.registers}")
            println("Input: $input")
            println("Mutable input: $input")
            println("Output: $output")
            println("_2019.Instruction pointer: $instructionPointer")
            println("Input: $input")
            throw e
        }
    }
}