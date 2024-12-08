package org.example

var failures = 0
var passes = 0

fun <T> test(
    name: String,
    body: () -> T,
    expected: T,
) {
    try {
        val value = body()
        if (value != expected) {
            failures++
            println("❌ [$name] Expected $expected, got $value")
        } else {
            passes++
            println("✅ [$name]")
        }
    } catch (e: Exception) {
        failures++
        println("❌ [$name] Expected $expected, crashed with $e")
    }
}

fun printTestSummary() {
    if (failures > 0) {
        println("❌ Failed $failures of ${failures + passes} tests")
    } else {
        println("✅ Passed $passes of ${failures + passes} tests")
    }
}
