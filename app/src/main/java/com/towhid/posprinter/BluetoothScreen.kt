package com.towhid.posprinter

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothScreen(viewModel: BluetoothViewModel = hiltViewModel()) {

    // Build permission list based on Android version
    val permissions = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            listOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    val permissionState = rememberMultiplePermissionsState(permissions = permissions)

    var hasRequestedPermissions by remember { mutableStateOf(false) }

    // Log permission status for debugging
    LaunchedEffect(permissionState.allPermissionsGranted) {
        Log.d("BluetoothScreen", "All permissions granted: ${permissionState.allPermissionsGranted}")
        permissionState.permissions.forEach { perm ->
            Log.d("BluetoothScreen", "${perm.permission}: granted=${perm.status.isGranted}, rationale=${perm.status.shouldShowRationale}")
        }
    }

    // Request permissions on first composition
    LaunchedEffect(Unit) {
        if (!hasRequestedPermissions) {
            Log.d("BluetoothScreen", "Requesting permissions...")
            hasRequestedPermissions = true
            permissionState.launchMultiplePermissionRequest()
        }
    }

    // Start Bluetooth operations when permissions are granted
    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted) {
            Log.d("BluetoothScreen", "Starting Bluetooth operations...")
            viewModel.loadPairedDevices()
            viewModel.startScan()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Debug info
        Text("Debug Info:")
        Text("All Permissions Granted: ${permissionState.allPermissionsGranted}")
        Text("Android Version: ${Build.VERSION.SDK_INT}")
        Spacer(Modifier.height(8.dp))

        permissionState.permissions.forEach { perm ->
            Text("${perm.permission.split(".").last()}: ${if (perm.status.isGranted) "✓" else "✗"}")
        }

        Spacer(Modifier.height(16.dp))

        if (permissionState.allPermissionsGranted) {
            // Show devices
            Text("Paired Devices (${viewModel.pairedDevices.size}):")
            Spacer(Modifier.height(8.dp))
            if (viewModel.pairedDevices.isEmpty()) {
                Text("No paired devices")
            } else {
                viewModel.pairedDevices.forEach { device ->
                    Text("• ${device.name ?: "Unknown"} - ${device.address}")
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Scanned Devices (${viewModel.scannedDevices.size}):")
            Spacer(Modifier.height(8.dp))
            if (viewModel.scannedDevices.isEmpty()) {
                Text("Scanning...")
            } else {
                viewModel.scannedDevices.forEach { device ->
                    Text("• ${device.name ?: "Unknown"} - ${device.address}")
                }
            }
        } else {
            Spacer(Modifier.height(16.dp))
            Text("⚠️ Permissions Required")
            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    Log.d("BluetoothScreen", "Manual permission request")
                    permissionState.launchMultiplePermissionRequest()
                }
            ) {
                Text("Request Permissions")
            }

            Spacer(Modifier.height(8.dp))
            Text("Required permissions:")
            permissions.forEach { perm ->
                Text("• ${perm.split(".").last()}")
            }
        }
    }
}