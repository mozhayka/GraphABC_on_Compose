package com.example.graphabc_on_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.tooling.preview.Preview
import kotlin.random.Random


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MessageCard(Message("Android", "Jetpack Compose"))
        }
    }
}

data class Message(val author: String, val body: String)

@Composable
fun MessageCard(msg: Message) {
    // Add padding around our message
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Image(
            painter = painterResource(R.drawable.profile_picture),
            contentDescription = "Contact profile picture",
            modifier = Modifier
                // Set image size to 40 dp
                .size(40.dp)
                // Clip image to be shaped as a circle
                .clip(CircleShape)
        )

        // Add a horizontal space between the image and the column
        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(text = msg.author)
            // Add a vertical space between the author and message texts
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = msg.body)
        }
    }
}

@Preview
@Composable
fun PreviewMessageCard() {
    MessageCard(
        msg = Message("Colleague", "Hey, take a look at Jetpack Compose, it's great!")
    )
}


class Game {
    private var previousTimeNanos: Long = Long.MAX_VALUE
    private val colors = arrayOf(
        Color.Red, Color.Blue, Color.Cyan,
        Color.Magenta, Color.Yellow, Color.Black
    )
    private var startTime = 0L

    var size by mutableStateOf(Pair(0.dp, 0.dp))

    var pieces = mutableStateListOf<PieceData>()
        private set

    var elapsed by mutableStateOf(0L)
    var score by mutableStateOf(0)
    private var clicked by mutableStateOf(0)

    var started by mutableStateOf(false)
    var paused by mutableStateOf(false)
    var finished by mutableStateOf(false)

    var numBlocks by mutableStateOf(5)

    fun start() {
        previousTimeNanos = System.nanoTime()
        startTime = previousTimeNanos
        clicked = 0
        started = true
        finished = false
        paused = false
        pieces.clear()
        repeat(numBlocks) { index ->
            pieces.add(PieceData(this, index * 1.5f + 5f, colors[index % colors.size]).also { piece ->
                piece.position = Random.nextDouble(0.0, 100.0).toFloat()
            })
        }
    }

    fun togglePause() {
        paused = !paused
        previousTimeNanos = System.nanoTime()
    }

    fun update(nanos: Long) {
        val dt = (nanos - previousTimeNanos).coerceAtLeast(0)
        previousTimeNanos = nanos
        elapsed = nanos - startTime
        pieces.forEach { it.update(dt) }
    }

    fun clicked(piece: PieceData) {
        score += piece.velocity.toInt()
        clicked++
        if (clicked == numBlocks) {
            finished = true
        }
    }
}

@Composable
@Preview
fun FallingBallsGame() {
    val game = remember { Game() }
    val density = LocalDensity.current
    Column {
        Text(
            "Catch balls!${if (game.finished) " Game over!" else ""}",
            fontSize = 50.sp,
            color = Color(218, 120, 91)
        )
        Text("Score ${game.score} Time ${game.elapsed / 1_000_000} Blocks ${game.numBlocks}", fontSize = 35.sp)
        Row {
            if (!game.started) {
                Slider(
                    value = game.numBlocks / 20f,
                    onValueChange = { game.numBlocks = (it * 20f).toInt().coerceAtLeast(1) },
                    modifier = Modifier.width(100.dp)
                )
            }
            Button(onClick = {
                game.started = !game.started
                if (game.started) {
                    game.start()
                }
            }) {
                Text(if (game.started) "Stop" else "Start", fontSize = 40.sp)
            }
            if (game.started) {
                Spacer(Modifier.padding(5.dp))
                Button(onClick = {
                    game.togglePause()
                }) {
                    Text(if (game.paused) "Resume" else "Pause", fontSize = 40.sp)
                }
            }
        }
        if (game.started) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .onSizeChanged {
                    with(density) {
                        game.size = it.width.toDp() to it.height.toDp()
                    }
                }
            ) {
                game.pieces.forEachIndexed { index, piece -> Piece(index, piece) }
            }
        }

        LaunchedEffect(Unit) {
            while (true) {
                withFrameNanos {
                    if (game.started && !game.paused && !game.finished)
                        game.update(it)
                }
            }
        }
    }
}

@Composable
fun Piece(index: Int, piece: PieceData) {
    val boxSize = 40.dp
    Box(
        Modifier
            .offset(boxSize * index * 5 / 3, piece.position.dp)
            .shadow(30.dp)
            .clip(CircleShape)
    ) {
        Box(
            Modifier
                .size(boxSize, boxSize)
                .background(if (piece.clicked) Color.Gray else piece.color)
                .clickable(onClick = { piece.click() })
        )
    }
}

data class PieceData(val game: Game, val velocity: Float, val color: Color) {
    var clicked by mutableStateOf(false)
    var position by mutableStateOf(0f)

    fun update(dt: Long) {
        if (clicked) return
        val delta = (dt / 1E8 * velocity).toFloat()
        position = if (position < game.size.second.value) position + delta else 0f
    }

    fun click() {
        if (!clicked && !game.paused) {
            clicked = true
            game.clicked(this)
        }
    }
}

//@Composable
//fun SmileyFaceCanvas(
//    modifier: Modifier
//) {
//    Canvas(
//        modifier = modifier.size(300.dp),
//        onDraw = {
//            // Head
//            drawCircle(
//                Brush.linearGradient(
//                    colors = listOf(greenLight700, green700)
//                ),
//                radius = size.width / 2,
//                center = center,
//                style = Stroke(width = size.width * 0.075f)
//            )
//
//            // Smile
//            val smilePadding = size.width * 0.15f
//            drawArc(
//                color = red700,
//                startAngle = 0f,
//                sweepAngle = 180f,
//                useCenter = true,
//                topLeft = Offset(smilePadding, smilePadding),
//                size = Size(size.width - (smilePadding * 2f), size.height - (smilePadding * 2f))
//            )
//
//            // Left eye
//            drawRect(
//                color = dark,
//                topLeft = Offset(size.width * 0.25f, size.height / 4),
//                size = Size(smilePadding, smilePadding)
//            )
//
//            // Right eye
//            drawRect(
//                color = dark,
//                topLeft = Offset((size.width * 0.75f) - smilePadding, size.height / 4),
//                size = Size(smilePadding, smilePadding)
//            )
//        }
//    )
//}
//
//@Composable
//fun DrawCircle()
//{
//    ctx.beginPath ();
//    ctx.arc (100, 50, 15, 0, Math.PI * 2, false);
//    ctx.stoke ();
//}

@Preview(showBackground = true)
@Composable
fun CanvasDrawExample() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(Color.Blue, topLeft = Offset(0f, 0f), size = Size(this.size.width, 55f))
        drawCircle(Color.Red, center = Offset(50f, 200f), radius = 40f)
        drawLine(
            Color.Green, Offset(20f, 0f),
            Offset(200f, 200f), strokeWidth = 5f
        )

        drawArc(
            Color.Black,
            0f,
            60f,
            useCenter = true,
            size = Size(300f, 300f),
            topLeft = Offset(60f, 60f)
        )
    }
}

@Composable
fun DrawCircle(color: Color, center_x: Float, center_y: Float, r: Float)
{
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(color, center = Offset(center_x, center_y), radius = r)
    }
}

@Preview(showBackground = true)
@Composable
fun CircleExample() {
    DrawCircle(color = Color.Blue, center_x = 100.5f, center_y = 120.7f, r = 40f)
}