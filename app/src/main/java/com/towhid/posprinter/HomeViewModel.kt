package com.towhid.posprinter

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.posprinter.IConnectListener
import net.posprinter.IDeviceConnection
import net.posprinter.POSConnect
import javax.inject.Inject

/*
@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    var selectedMacAddress by mutableStateOf<String?>(null)
        private set


    fun getDeviceConnection(): IDeviceConnection? = deviceConnection
    private var deviceConnection: IDeviceConnection? = null

    private val connectListener = IConnectListener { code, connInfo, msg ->
        viewModelScope.launch {
            when (code) {
                POSConnect.CONNECT_SUCCESS -> {
                    _connectionState.value = ConnectionState.Connected
                    _toastMessage.value = "Connected successfully"
                }
                POSConnect.CONNECT_FAIL -> {
                    _connectionState.value = ConnectionState.Failed(msg ?: "Connection failed")
                    _toastMessage.value = "Connection failed"
                }
                POSConnect.CONNECT_INTERRUPT -> {
                    _connectionState.value = ConnectionState.Disconnected
                    _toastMessage.value = "Connection interrupted"
                }
                POSConnect.SEND_FAIL -> {
                    _toastMessage.value = "Send failed"
                }
                POSConnect.USB_DETACHED -> {
                    _connectionState.value = ConnectionState.Disconnected
                    _toastMessage.value = "USB detached"
                }
                POSConnect.USB_ATTACHED -> {
                    _toastMessage.value = "USB attached"
                }
            }
        }
    }

    fun setMacAddress(macAddress: String) {
        selectedMacAddress = macAddress
    }

    fun connectToPrinter(macAddress: String) {
        viewModelScope.launch {
            try {
                _connectionState.value = ConnectionState.Connecting

                // Initialize IDeviceConnection
                deviceConnection = POSConnect.createDevice(POSConnect.DEVICE_TYPE_BLUETOOTH)
                deviceConnection?.connect(macAddress, connectListener)

            } catch (e: Exception) {
                _connectionState.value = ConnectionState.Failed(e.message ?: "Unknown error")
                _toastMessage.value = "Connection error: ${e.message}"
            }
        }
    }


}

sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Connecting : ConnectionState()
    object Connected : ConnectionState()
    data class Failed(val error: String) : ConnectionState()
}*/


@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val deviceConnection: IDeviceConnection
) : AndroidViewModel(application) {

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    var selectedMacAddress by mutableStateOf<String?>(null)
        private set

    private val connectListener = IConnectListener { code, connInfo, msg ->
        viewModelScope.launch {
            when (code) {
                POSConnect.CONNECT_SUCCESS -> {
                    _connectionState.value = ConnectionState.Connected
                    _toastMessage.value = "Connected successfully"
                }
                POSConnect.CONNECT_FAIL -> {
                    _connectionState.value = ConnectionState.Failed(msg ?: "Connection failed")
                    _toastMessage.value = "Connection failed"
                }
                POSConnect.CONNECT_INTERRUPT -> {
                    _connectionState.value = ConnectionState.Disconnected
                    _toastMessage.value = "Connection interrupted"
                }
                POSConnect.SEND_FAIL -> {
                    _toastMessage.value = "Send failed"
                }
                POSConnect.USB_DETACHED -> {
                    _connectionState.value = ConnectionState.Disconnected
                    _toastMessage.value = "USB detached"
                }
                POSConnect.USB_ATTACHED -> {
                    _toastMessage.value = "USB attached"
                }
            }
        }
    }

    fun setMacAddress(macAddress: String) {
        selectedMacAddress = macAddress
    }

    fun connectToPrinter(macAddress: String) {
        viewModelScope.launch {
            try {
                _connectionState.value = ConnectionState.Connecting

                // Connect using injected device connection
                deviceConnection.connect(macAddress, connectListener)

            } catch (e: Exception) {
                _connectionState.value = ConnectionState.Failed(e.message ?: "Unknown error")
                _toastMessage.value = "Connection error: ${e.message}"
            }
        }
    }
/*
    fun disconnect() {
        viewModelScope.launch {
            try {
                deviceConnection.closeConnection()
                _connectionState.value = ConnectionState.Disconnected
                _toastMessage.value = "Disconnected"
            } catch (e: Exception) {
                _toastMessage.value = "Disconnect error: ${e.message}"
            }
        }
    }*/


    fun clearToast() {
        _toastMessage.value = null
    }

    /*override fun onCleared() {
        super.onCleared()
        deviceConnection.closeConnection()
    }*/
}

sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Connecting : ConnectionState()
    object Connected : ConnectionState()
    data class Failed(val error: String) : ConnectionState()
}