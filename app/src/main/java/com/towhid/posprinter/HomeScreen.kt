package com.towhid.posprinter

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

/*
@Composable
fun HomeScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val macAddress by savedStateHandle
        ?.getStateFlow<String?>("selected_mac", null)
        ?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(null) }

    // Debug logging
    LaunchedEffect(macAddress) {
        Log.d("HomeScreen", "Received MAC Address: $macAddress")
    }

    var curConnect: IDeviceConnection? = null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "POS Printer",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(32.dp))

        // Display selected MAC address
        if (macAddress != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Selected Device",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = macAddress!!,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        } else {
            Text(
                text = "No device selected",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
        }

        Button(
            onClick = {
                Log.d("HomeScreen", "Navigating to Bluetooth screen")
                navController.navigate("bluetooth")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (macAddress == null) "Select Bluetooth Device" else "Change Device")
        }

        if (macAddress != null) {
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    // Connect to printer and print
                    // viewModel.connectAndPrint(macAddress!!)

                    val bleAddress = macAddress
                    if (bleAddress == "") {
                        Toast.makeText(context, "Please Select Device", Toast.LENGTH_SHORT).show()
                    } else {
                        //App.get().connectBt(bleAddress)

                        curConnect?.close()
                        curConnect = POSConnect.createDevice(POSConnect.DEVICE_TYPE_BLUETOOTH)
                        curConnect!!.connect(macAddress, connectListener)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Print Test")
            }
        }
    }
}*/


@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Listen for result from Bluetooth screen
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val receivedMacAddress by savedStateHandle
        ?.getStateFlow<String?>("selected_mac", null)
        ?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(null) }

    // Update ViewModel when MAC address is received
    LaunchedEffect(receivedMacAddress) {
        receivedMacAddress?.let {
            Log.d("HomeScreen", "Received MAC Address: $it")
            viewModel.setMacAddress(it)
            // Clear the saved state after reading
            savedStateHandle?.set("selected_mac", null)
        }
    }

    // Observe connection state
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val macAddress = viewModel.selectedMacAddress

    // Show toast messages
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            /*viewModel.clearToast()*/
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "POS Printer",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(32.dp))

        // Connection Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = when (connectionState) {
                    is ConnectionState.Connected -> MaterialTheme.colorScheme.primaryContainer
                    is ConnectionState.Connecting -> MaterialTheme.colorScheme.secondaryContainer
                    is ConnectionState.Failed -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (connectionState) {
                        is ConnectionState.Connecting -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Text("Connecting...")
                        }
                        is ConnectionState.Connected -> {
                            Text("✓ Connected", style = MaterialTheme.typography.bodyLarge)
                        }
                        is ConnectionState.Failed -> {
                            Text(
                                "✗ Failed: ${(connectionState as ConnectionState.Failed).error}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        else -> {
                            Text("Disconnected", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Selected Device Card
        if (macAddress != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Selected Device",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = macAddress,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No device selected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // Select Device Button
        Button(
            onClick = {
                Log.d("HomeScreen", "Navigating to Bluetooth screen")
                navController.navigate("bluetooth")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (macAddress == null) "Select Bluetooth Device" else "Change Device")
        }

        // Connect/Disconnect Button
        if (macAddress != null) {
            Spacer(Modifier.height(8.dp))

            when (connectionState) {
                is ConnectionState.Connected -> {
                    /*Button(
                        onClick = { *//*viewModel.disconnect()*//* },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Disconnect")
                    }*/
                }
                is ConnectionState.Connecting -> {
                    Button(
                        onClick = { },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Connecting...")
                    }
                }
                else -> {
                    Button(
                        onClick = { viewModel.connectToPrinter(macAddress) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Connect")
                    }
                }
            }
        }

        // Print Test Button
        if (connectionState is ConnectionState.Connected) {
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { navController.navigate("pos_print") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text("Print Test")
            }
        }

        // Clear Selection Button
        if (macAddress != null && connectionState !is ConnectionState.Connected) {
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { viewModel.setMacAddress("") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear Selection")
            }
        }
    }
}