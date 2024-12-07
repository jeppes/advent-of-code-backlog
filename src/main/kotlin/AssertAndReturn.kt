package org.example

fun <T> assertAndReturn(value: T, expected: T): T {
  if (value != expected) {
    println("❌ Expected $expected, got $value")
    throw AssertionError("Expected $expected, got $value")
  }
  return value
}
