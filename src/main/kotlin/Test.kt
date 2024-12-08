package org.example

fun <T> test(
    name: String,
    body: () -> T,
    expected: T,
) {
    try {
        val value = body()
        if (value != expected) {
            println("❌ [$name] Expected $expected, got $value")
        } else {
            println("✅ [$name]")
        }
    } catch (e: Exception) {
        println("❌ [$name] Expected $expected, crashed with $e")
    }
}
