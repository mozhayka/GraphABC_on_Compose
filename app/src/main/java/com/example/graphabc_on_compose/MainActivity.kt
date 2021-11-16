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
import androidx.compose.ui.graphics.Color
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

//@Preview
@Composable
fun PreviewMessageCard() {
    MessageCard(
        msg = Message("Colleague", "Hey, take a look at Jetpack Compose, it's great!")
    )
}


//@Preview(showBackground = true)
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

class Person(_name: String){
    val name: String
    init{
        name = _name
    }
}

//class Circle(color: Color, center_x: Float, center_y: Float, r: Float) {
//    init{
//        Canvas(modifier = Modifier.fillMaxSize()) {
//            drawCircle(color, center = Offset(center_x, center_y), radius = r)
//        }
//        DrawCircle(color, center_x, center_y, r)
//    }
//}

class Circle(private val color: Color, private val center_x: Float, private val center_y: Float, private val r: Float) : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(color, center = Offset(center_x, center_y), radius = r)
            }
        }
    }
}

@Composable
fun DrawCircle(color: Color, center_x: Float, center_y: Float, r: Float)
{
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(color, center = Offset(center_x, center_y), radius = r)
    }
}

@Composable
fun DrawRect(color: Color, topLeft_x: Float, topLeft_y: Float, size: Size)
{
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(color, topLeft = Offset(topLeft_x, topLeft_y), size = size)
    }
}

@Preview(showBackground = true)
@Composable
fun CircleExample() {
    DrawCircle(color = Color.Blue, center_x = 100.5f, center_y = 120.7f, r = 40f)
    DrawCircle(color = Color.Red, center_x = 80.5f, center_y = 120f, r = 40f)
    DrawRect(color = Color.Gray, topLeft_x = 80.5f, topLeft_y = 120f, size = Size(40f, 60f))
    DrawRect(color = Color.Black, topLeft_x = 240.5f, topLeft_y = 200f, size = Size(80f, 200f))
    //val circle = Circle(color = Color.Red, center_x = 180.5f, center_y = 220f, r = 40f)
}

