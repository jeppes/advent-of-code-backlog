package org.example._2015

import org.example.assertAndReturn
import org.example.measure
import org.example.readFile

private fun part1(input: String): Int {
  return input.lines().count { line ->
    val vowels = arrayOf('a', 'e', 'i', 'o', 'u')

    // It contains at least three vowels (aeiou only), like aei, xazegov, or aeiouaeiouaeiou.
    val atLeast3Vowels = line.count { char -> vowels.contains(char) } >= 3

    // It contains at least one letter that appears twice in a row, like xx, abcdde (dd), or
    // aabbccdd (aa, bb, cc, or dd).
    val atLeastOneDoubleLetter = line.zip(line.drop(1)).any { (c1, c2) -> c1 == c2 }

    // It does not contain the strings ab, cd, pq, or xy, even if they are part of one of the
    // other requirements.
    val doesNotContainIllegalCharacters =
        line.zip(line.drop(1)).none { (c1, c2) ->
          c1 == 'a' && c2 == 'b' ||
              c1 == 'c' && c2 == 'd' ||
              c1 == 'p' && c2 == 'q' ||
              c1 == 'x' && c2 == 'y'
        }

    return@count atLeast3Vowels && atLeastOneDoubleLetter && doesNotContainIllegalCharacters
  }
}

private fun isNice(line: String): Boolean {
  // It contains a pair of any two letters that appears at least twice in the string without
  // overlapping, like xyxy (xy) or aabcdefgaa (aa), but not like aaa (aa, but it overlaps).

  val atLeastOneDoubleLetter = run {
    val pairs = mutableMapOf<String, Int>()
    line.zip(line.drop(1)).forEachIndexed { index, (c1, c2) ->
      val pair = "$c1$c2"

      if (pairs.contains(pair)) {
        if (pairs[pair]!! < index - 1) {
          return@run true
        }
      } else {
        pairs[pair] = index
      }
    }
    return@run false
  }

  // It contains at least one letter which repeats with exactly one letter between them, like xyx,
  // abcdefeghi (efe), or even aaa.
  val oneRepeatedLetterWithInBetween = line.zip(line.drop(2)).any { (c1, c3) -> c1 == c3 }
  return atLeastOneDoubleLetter && oneRepeatedLetterWithInBetween
}

private fun part2(input: String): Int {
  return input.lines().count { line ->
    return@count isNice(line)
  }
}

fun main() {
  val input = readFile("/2015_5.txt")
  measure("Part 1") { assertAndReturn(part1(input), 238) }
  measure("Part 2") { assertAndReturn(part2(input), 69) }
}
