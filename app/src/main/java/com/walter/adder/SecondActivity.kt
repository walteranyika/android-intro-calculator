package com.walter.adder


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
import android.text.format.DateFormat
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.walter.adder.dtos.SmsMessage
import java.util.*


class SecondActivity : ComponentActivity() {

    private lateinit var onMessagesLoaded: (List<SmsMessage>) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val smsPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                val messages = readMessagesFromSender("MPESA", keyword = "confirmed")
                onMessagesLoaded(messages)
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        setContent {
            var messages by remember { mutableStateOf<List<SmsMessage>>(emptyList()) }
            val context = LocalContext.current

            onMessagesLoaded = {
                messages = it
            }

            // Ask permission
            LaunchedEffect(Unit) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_SMS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    smsPermissionLauncher.launch(Manifest.permission.READ_SMS)
                } else {
                    messages = readMessagesFromSender("MPESA", keyword = "confirmed")
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
                            text = "Filtered MPESA Messages",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        if (messages.isEmpty()) {
                            Text("No matching messages found or permission denied.")
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(messages) { msg ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        elevation = CardDefaults.cardElevation(4.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(
                                                text = formatTimestamp(msg.timestamp),
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = msg.body,
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
    }

    // Function to read and filter messages
    private fun readMessagesFromSender(sender: String, keyword: String? = null): List<SmsMessage> {
        val messages = mutableListOf<SmsMessage>()
        val uri = Telephony.Sms.Inbox.CONTENT_URI
        val projection = arrayOf(Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE)
        val selection = "${Telephony.Sms.ADDRESS} = ?"
        val selectionArgs = arrayOf(sender)

        contentResolver.query(uri, projection, selection, selectionArgs, "date DESC")?.use { cursor ->
            val bodyIndex = cursor.getColumnIndex(Telephony.Sms.BODY)
            val dateIndex = cursor.getColumnIndex(Telephony.Sms.DATE)

            while (cursor.moveToNext()) {
                val body = cursor.getString(bodyIndex)
                val date = cursor.getLong(dateIndex)

                if (keyword == null || body.contains(keyword, ignoreCase = true)) {
                    messages.add(SmsMessage(body = body, timestamp = date))
                }
            }
        }

        return messages
    }

    // Format the timestamp
    private fun formatTimestamp(timestamp: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        return DateFormat.format("dd MMM yyyy hh:mm a", cal).toString()
    }
}
