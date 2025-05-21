package com.walter.adder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

class SecondActivity : ComponentActivity() {

    private lateinit var onMessagesLoaded: (List<String>) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SMS permission launcher
        val smsPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                val messages = readMessagesFromSender("MPESA") // Change sender as needed
                onMessagesLoaded(messages)
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        setContent {
            var messages by remember { mutableStateOf<List<String>>(emptyList()) }
            val context = LocalContext.current

            // Provide callback
            onMessagesLoaded = {
                messages = it
            }

            // Launch permission request if not granted
            LaunchedEffect(Unit) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_SMS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    smsPermissionLauncher.launch(Manifest.permission.READ_SMS)
                } else {
                    messages = readMessagesFromSender("MPESA")
                }
            }

            MaterialTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Messages from MPESA",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        if (messages.isEmpty()) {
                            Text("No messages found or permission denied.")
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(messages) { msg ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        elevation = CardDefaults.cardElevation(4.dp)
                                    ) {
                                        Text(
                                            text = msg,
                                            modifier = Modifier.padding(16.dp),
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Function to read messages from a specific sender
    private fun readMessagesFromSender(sender: String): List<String> {
        val messages = mutableListOf<String>()
        val uri = Telephony.Sms.Inbox.CONTENT_URI
        val projection = arrayOf(Telephony.Sms.ADDRESS, Telephony.Sms.BODY)
        val selection = "${Telephony.Sms.ADDRESS} = ?"
        val selectionArgs = arrayOf(sender)

        contentResolver.query(uri, projection, selection, selectionArgs, "date DESC")
            ?.use { cursor ->
                val bodyIndex = cursor.getColumnIndex(Telephony.Sms.BODY)

                while (cursor.moveToNext()) {
                    val body = cursor.getString(bodyIndex)
                    messages.add(body)
                }
            }

        return messages
    }
}
