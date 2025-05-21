package com.walter.adder

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.walter.adder.ui.theme.AdderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdderTheme {
                //AddCalculator()
                AdvancedCalculator()
            }
        }
    }
}

@Composable
fun AdvancedCalculator() {
    var number1 by remember { mutableStateOf("") }
    var number2 by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String>("") }

    val operations = listOf("Add", "Subtract", "Multiply", "Divide")

    var selectedOperation by remember { mutableStateOf(operations[0]) }
    var expanded by remember { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current

    val context = LocalContext.current

    Spacer(modifier = Modifier.height(24.dp))



    val resultAlpha by animateFloatAsState(
        targetValue = if (result != null) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "Result Alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F8))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Card(
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("Cool Calculator", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = number1,
                    onValueChange = { number1 = it },
                    label = { Text("Enter First Numbber") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = number2,
                    onValueChange = { number2 = it },
                    label = { Text("Enter Second Number") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Box(modifier = Modifier.fillMaxWidth())
                {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Operation $selectedOperation")
                    }

                    DropdownMenu(expanded = expanded,
                        onDismissRequest = { expanded = false }) {
                        operations.forEach { operation ->
                            @androidx.compose.runtime.Composable {
                                DropdownMenuItem(onClick = {
                                    selectedOperation = operation
                                    expanded = false
                                }, text = { Text(operation) })
                            }
                        }
                    }

                }

                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                        val num1 = number1.toDoubleOrNull()
                        val num2 = number2.toDoubleOrNull()
                        result = if (num1 != null && num2 != null) {
                            when (selectedOperation) {
                                "Add" -> "Result :${num1 + num2}"
                                "Subtract" -> "Result ${num1 - num2}"
                                "Multi[ply" -> "Result ${num1 * num2}"
                                "Divide" -> "Result ${num1 / num2}"
                                else -> "Unkown Operation"
                            }
                        } else {
                            "Please enter valid number"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Calculate", color = Color.White, fontSize = 18.sp)
                }

                Button(
                    onClick = {
                        context.startActivity(Intent(context, SecondActivity::class.java))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Go to Second Activity", color = Color.White, fontSize = 16.sp)
                }

                if (result != null){
                    AnimatedVisibility(visible = true,
                        enter = fadeIn(tween(500)),
                        exit = fadeOut(tween(300))
                    ) {
                        Text(
                            text = result?:"",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (result.startsWith("Result")) Color(0xFF2E7D32) else Color.Red,
                            modifier = Modifier.alpha(resultAlpha)
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun AddCalculator() {
    var number1 by remember { mutableStateOf("") }
    var number2 by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String>("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = number1,
            onValueChange = { number1 = it },
            label = { Text("Enter A Number") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = number2,
            onValueChange = { number2 = it },
            label = { Text("Enter Second Number") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val num1 = number1.toDoubleOrNull()
                val num2 = number1.toDoubleOrNull()
                if (num1 != null && num2 != null) {
                    val ans = num1 + num2
                    result = "Result is $ans"
                } else {
                    result = "Enter Valid Numbers"
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add", color = Color.White, fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))

        result?.let {
            Text(
                text = it,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (it.startsWith("Result")) Color(0xFF2E7D32) else Color.Red
            )
        }


    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AdderTheme {
        Greeting("Android")
    }
}