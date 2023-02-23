import java.awt.Color
import java.awt.image.BufferedImage

fun drawPolygon(): BufferedImage {
    val imageWidth = 300

    val image = BufferedImage(imageWidth, imageWidth, BufferedImage.TYPE_INT_RGB)

    val graphics = image.graphics

    graphics.color = Color.YELLOW
    graphics.drawPolygon(
        intArrayOf(50, 100, 200, 250, 200, 100),
        intArrayOf(150, 250, 250, 150, 50, 50),
        6
    )

    return image
}