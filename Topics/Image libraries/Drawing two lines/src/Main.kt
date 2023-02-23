import java.awt.Color
import java.awt.image.BufferedImage  

fun drawLines(): BufferedImage {
    val imageWidth = 200

    val image = BufferedImage(
        imageWidth,
        imageWidth,
        BufferedImage.TYPE_INT_RGB
    )

    val graphics = image.graphics

    graphics.color = Color.RED
    graphics.drawLine(0, 0, 200, 200)

    graphics.color = Color.GREEN
    graphics.drawLine(200, 0, 0, 200)

    return image
}