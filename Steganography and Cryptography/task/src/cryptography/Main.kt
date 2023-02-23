package cryptography

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import javax.imageio.ImageIO
import kotlin.experimental.xor

fun main() {
    while (true) {
        println("Task (hide, show, exit): ")
        when (val task = readln()) {
            "exit" -> {
                println("Bye!")
                break
            }
            "hide" -> {
                println("Input image file: ")
                val inputFile = File(readln())
                println("Output image file: ")
                val outputFile = File(readln())
                println("Message to hide: ")
                val message = readln()
                try {
                    hide(inputFile, outputFile, message)
                    println("Message saved in $outputFile image.")
                } catch (e: Exception) {
                    println(e.message)
                }
            }
            "show" -> {
                println("Input image file: ")
                val inputFile = File(readln())
                val message = show(inputFile)
                println("Message:")
                println(message)
            }
            else -> {
                println("Wrong task: [$task]")
            }
        }
    }
}

fun hide(inputFile: File, outputFile: File, message: String) {
    println("Password:")
    val password = readln()

    val messageBytes = message.encodeToByteArray()
    val passwordBytes = password.encodeToByteArray()

    var passwordBytesIndex = 0
    val encryptedMessageBytes = String(ByteArray(messageBytes.size + 3)).encodeToByteArray()

    messageBytes.withIndex().forEach { byte ->
        encryptedMessageBytes[byte.index] = byte.value xor passwordBytes[passwordBytesIndex]
        passwordBytesIndex++

        if (passwordBytesIndex > passwordBytes.size - 1) { passwordBytesIndex = 0 }
    }

    encryptedMessageBytes[encryptedMessageBytes.lastIndex] = 3
    encryptedMessageBytes[encryptedMessageBytes.lastIndex - 1] = 0
    encryptedMessageBytes[encryptedMessageBytes.lastIndex - 2] = 0

    val bits = encryptedMessageBytes.map { byte -> (0..7).map { byte.toInt() shl it and 0xFF shr 7 } }.flatten().toIntArray()
    val image = ImageIO.read(inputFile)

    if (image.width * image.height < bits.size) {
        throw IllegalArgumentException("The input image is not large enough to hold this message.")
    }

    bits.withIndex().forEach { bit ->
        val x = bit.index % image.width
        val y = bit.index / image.width
        val modified = image.getRGB(x,y).toUInt() and 0xFFFFFFFEu or bit.value.toUInt()
        image.setRGB(x, y, modified.toInt())
    }

    ImageIO.write(image, "png", outputFile)
}

//TODO reconstruct a message
fun show(inputFile: File): String {
    val image = ImageIO.read(inputFile)
    val bytes = mutableListOf<Int>()
    for (byte in generateSequence(0) { it + 1 }
        .map {
            val x = it % image.width
            val y = it / image.width
            image.getRGB(x, y) and 1
        }
        .chunked(8)
        .map { it.reduce { byte, i -> byte shl 1 or i } }) {
        bytes.add(byte)
        val lastIndex = bytes.lastIndex
        if (bytes.size >= 3 && bytes[lastIndex] == 3 && bytes[lastIndex - 1] == 0 && bytes[lastIndex - 2] == 0) {
            break
        }
    }

    val encryptedMessage = bytes.dropLast(3)
    val encryptedMessageByteArray = ByteArray(encryptedMessage.size) { 0 }
    val decryptedMessageByteArray = ByteArray(encryptedMessage.size) { 0 }

    println("Password:")
    val password = readln()
    val passwordBytes = password.encodeToByteArray()

    encryptedMessage.withIndex().forEach { byte ->
        encryptedMessageByteArray[byte.index] = byte.value.toByte()
    }

    var passwordBytesIndex = 0
    encryptedMessageByteArray.withIndex().forEach { byte ->
        decryptedMessageByteArray[byte.index] = byte.value xor passwordBytes[passwordBytesIndex]
        passwordBytesIndex++

        if (passwordBytesIndex > passwordBytes.size - 1) { passwordBytesIndex = 0 }
    }



    return decryptedMessageByteArray.toString(UTF_8)
}

