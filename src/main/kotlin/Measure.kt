package org.example

import kotlin.time.measureTimedValue

fun <T> measure(label: String, block: () -> T): T {
  val result = measureTimedValue(block)
  println("[$label] ${result.value} (${result.duration})")
  return result.value
}
