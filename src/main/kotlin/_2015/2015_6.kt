import kotlin.math.max

private fun part1(input: String): Int {
    val switches = Array(1000) { Array(1000) { false } }

    for (line in input.lines()) {
        val numbersInLine = line
            .split(Regex("[^\\d+]"))
            .filterNot { it.isBlank() }
            .map { it.toInt() }
        val row1 = numbersInLine[0]
        val col1 = numbersInLine[1]
        val row2 = numbersInLine[2]
        val col2 = numbersInLine[3]

        for (rowIndex in row1..row2) {
            for (colIndex in col1..col2) {
                if (line.startsWith("toggle")) {
                    switches[rowIndex][colIndex] = !switches[rowIndex][colIndex]
                } else if (line.startsWith("turn off")) {
                    switches[rowIndex][colIndex] = false
                } else if (line.startsWith("turn on")) {
                    switches[rowIndex][colIndex] = true
                }
            }
        }
    }

    return switches.sumOf { it.count { on -> on } }
}


private fun part2(input: String): Int {
    val switches = Array(1000) { Array(1000) { 0 } }

    for (line in input.lines()) {
        val numbersInLine = line
            .split(Regex("[^\\d+]"))
            .filterNot { it.isBlank() }
            .map { it.toInt() }
        val row1 = numbersInLine[0]
        val col1 = numbersInLine[1]
        val row2 = numbersInLine[2]
        val col2 = numbersInLine[3]

        for (rowIndex in row1..row2) {
            for (colIndex in col1..col2) {
                if (line.startsWith("toggle")) {
                    switches[rowIndex][colIndex] += 2
                } else if (line.startsWith("turn off")) {
                    switches[rowIndex][colIndex] = max(0, switches[rowIndex][colIndex] - 1)
                } else if (line.startsWith("turn on")) {
                    switches[rowIndex][colIndex] += 1
                }
            }
        }
    }

    return switches.sumOf { it.sumOf { light -> light } }
}

fun main() {
    val input = readFile("/2015_6.txt")
    measure("Part 1") { assertAndReturn(part1(input), 543903) }
    measure("Part 2") { assertAndReturn(part2(input), 14687245) }
}
