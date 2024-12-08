data class IntcodeResult(
    val output: List<Int>,
)

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
    abstract val state: IntcodeState

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