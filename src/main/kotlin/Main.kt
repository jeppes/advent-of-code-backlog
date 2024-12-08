fun main() {}

object Main

fun readFile(name: String): String {
    try {
        val file = Main::class.java.getResource(name)!!.readText()
        return file
    } catch (e: NullPointerException) {
        println("❌ file $name not found")
        throw AssertionError("File $name not found")
    }
}
