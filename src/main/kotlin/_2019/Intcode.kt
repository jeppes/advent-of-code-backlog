data class IntcodeResult(
    val state: List<Int>,
    val output: List<Int>,
)

fun parseIntcodeState(input: String): List<Int> {
    return input.split(",").map { it.toInt() }
}

interface Intcode {
    fun run(
        startState: List<Int>,
        input: List<Int>
    ): IntcodeResult

    fun run(
        startString: String,
        input: List<Int>
    ): IntcodeResult {
        return run(parseIntcodeState(startString), input = input)
    }
}