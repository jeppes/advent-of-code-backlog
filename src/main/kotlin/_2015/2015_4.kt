package _2015

import assertAndReturn
import measure
import readFile
import java.security.MessageDigest

@OptIn(ExperimentalStdlibApi::class)
fun md5(string: String): String {
    return MessageDigest.getInstance("MD5").digest(string.toByteArray()).toHexString()
}

private fun solve(line: String, numberOfZeroes: Int): Int {
    var i = 0

    while (true) {
        if (md5("$line$i").take(numberOfZeroes).all { it == '0' }) {
            return i
        }

        i++
    }
}

fun main() {
    val input = readFile("/2015_4.txt")
    measure("Part 1") { assertAndReturn(solve(input, numberOfZeroes = 5), 346386) }
    measure("Part 2") { assertAndReturn(solve(input, numberOfZeroes = 6), 9958218) }
}
