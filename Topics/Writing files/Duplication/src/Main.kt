import java.io.File

// Write your code here. Do not import any libraries

fun main() {
    val text = readLine()!!
    val myFile = File("MyFile.txt")

    repeat(2) {
        myFile.appendText(text)
    }
}