package _2019

import printTestSummary
import readFile
import test


fun day5Tests(intcode: Intcode) {
    // Examples in part 1
    run {
        // The program 3,0,4,0,99 outputs whatever it gets as input, then halts.
        val example = "3,0,4,0,99"
        test(
            "Prints input 5",
            { intcode.run(example, input = listOf(5)).output },
            expected = listOf(5)
        )
        test(
            "Prints the input 15",
            { intcode.run(example, input = listOf(15)).output },
            expected = listOf(15)
        )
    }

    // Full part 1
    run {
        test(
            "Day 5 Part 1",
            { intcode.run(readFile("/2019_5.txt"), input = listOf(1)).output },
            expected = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 15508323)
        )
    }

    // Examples in part 2
    run {
        // 3,9,8,9,10,9,4,9,99,-1,8 - Using position mode, consider whether the input is
        // equal to 8; output 1 (if it is) or 0 (if it is not).
        test(
            "Equal - Position mode, input 8 -> 1",
            { intcode.run("3,9,8,9,10,9,4,9,99,-1,8", input = listOf(8)).output.last() },

            expected = 1
        )
        test(
            "Equal - Position mode input != 8 -> 0",
            { intcode.run("3,9,8,9,10,9,4,9,99,-1,8", input = listOf(4)).output.last() },
            expected = 0
        )

        // 3,9,7,9,10,9,4,9,99,-1,8 - Using position mode, consider whether the input is
        // less than 8; output 1 (if it is) or 0 (if it is not).
        test(
            "Less Than - Position mode, input < 8 -> 1",
            { intcode.run("3,9,7,9,10,9,4,9,99,-1,8", input = listOf(7)).output },
            expected = listOf(1)
        )
        test(
            "Less Than - Position mode, input = 8 -> 0",
            { intcode.run("3,9,7,9,10,9,4,9,99,-1,8", input = listOf(8)).output },
            expected = listOf(0)
        )
        test(
            "Less Than - Position mode, input = 9 -> 0",
            { intcode.run("3,9,7,9,10,9,4,9,99,-1,8", input = listOf(9)).output },
            expected = listOf(0)
        )

        // 3,3,1108,-1,8,3,4,3,99 - Using immediate mode, consider whether the input is
        // equal to 8; output 1 (if it is) or 0 (if it is not).
        test(
            "Equal - Immediate mode, input = 8 -> 1",
            { intcode.run("3,3,1108,-1,8,3,4,3,99", input = listOf(8)).output },
            expected = listOf(1)
        )
        test(
            "Equal - Immediate mode, input = 7 -> 0",
            { intcode.run("3,3,1108,-1,8,3,4,3,99", input = listOf(7)).output },
            expected = listOf(0)
        )

        // 3,3,1107,-1,8,3,4,3,99 - Using immediate mode, consider whether the input is
        // less than 8; output 1 (if it is) or 0 (if it is not).
        test(
            "Less Than - Immediate mode, input = 7 -> 1",
            { intcode.run("3,3,1107,-1,8,3,4,3,99", input = listOf(7)).output },
            expected = listOf(1)
        )
        test(
            "Less Than - Immediate mode, input = 8 -> 0",
            { intcode.run("3,3,1107,-1,8,3,4,3,99", input = listOf(8)).output },
            expected = listOf(0)
        )

        test(
            "Less Than - Immediate mode, input = 8 -> 0",
            { intcode.run("3,3,1107,-1,8,3,4,3,99", input = listOf(9)).output },
            expected = listOf(0)
        )

        // Here are some jump tests that take an input, then output 0 if the input
        // was zero or 1 if the input was non-zero:
        //  3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9 (using position mode)
        //  3,3,1105,-1,9,1101,0,0,12,4,12,99,1 (using immediate mode)
        test(
            "Jump - Position mode, input = 0 -> 0",
            { intcode.run("3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9", input = listOf(0)).output },
            expected = listOf(0)
        )
        test(
            "Jump - Position mode, input = 1 -> 1",
            { intcode.run("3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9", input = listOf(1)).output },
            expected = listOf(1)
        )
        test(
            "Jump - Position mode, input = -1 -> 1",
            { intcode.run("3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9", input = listOf(-1)).output },
            expected = listOf(1)
        )
        test(
            "Jump - Immediate mode, input = 0 -> 0",
            { intcode.run("3,3,1105,-1,9,1101,0,0,12,4,12,99,1", input = listOf(0)).output },
            expected = listOf(0)
        )
        test(
            "Jump - Immediate mode, input = 1 -> 1",
            { intcode.run("3,3,1105,-1,9,1101,0,0,12,4,12,99,1", input = listOf(1)).output },
            expected = listOf(1)
        )
        test(
            "Jump - Immediate mode, input = -1 -> 1",
            { intcode.run("3,3,1105,-1,9,1101,0,0,12,4,12,99,1", input = listOf(-1)).output },
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
            { intcode.run(longProgram, input = listOf(7)).output },
            expected = listOf(999)
        )
        test(
            "Long program - input = 8 -> 1000",
            { intcode.run(longProgram, input = listOf(8)).output },
            expected = listOf(1000)
        )
        test(
            "Long program - input = 9 -> 1001",
            { intcode.run(longProgram, input = listOf(9)).output },
            expected = listOf(1001)
        )
    }

    // Full part 2
    run {
        test(
            "Day 5 - Part 2",
            { intcode.run(readFile("/2019_5.txt"), input = listOf(5)).output },
            expected = listOf(9006327)
        )
    }
}


fun main() {
    day2Tests(mainIntcode)
    day5Tests(mainIntcode)
    printTestSummary()
}

