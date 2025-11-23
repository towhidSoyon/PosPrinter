package com.towhid.posprinter

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.posprinter.IDeviceConnection
import net.posprinter.POSConst
import net.posprinter.POSPrinter
import net.posprinter.model.PTable
import java.nio.charset.Charset
import javax.inject.Inject

@HiltViewModel
class PosPrintViewModel @Inject constructor(
    application: Application,
    private val deviceConnection: IDeviceConnection
) : AndroidViewModel(application) {

    private val printer = POSPrinter(deviceConnection)

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun printText() {
        viewModelScope.launch {
            try {
                val str = "Welcome to the printer, this is print test content!\n"
                printer.initializePrinter()
                    .printString(str)
                    .printText(
                        "printText Demo\n",
                        POSConst.ALIGNMENT_CENTER,
                        POSConst.FNT_BOLD or POSConst.FNT_UNDERLINE,
                        POSConst.TXT_1WIDTH or POSConst.TXT_2HEIGHT
                    )
                    .cutHalfAndFeed(1)

                _toastMessage.value = "Text printed successfully"
            } catch (e: Exception) {
                _toastMessage.value = "Print failed: ${e.message}"
            }
        }
    }

    fun printBarcode() {
        viewModelScope.launch {
            try {
                printer.initializePrinter()
                    .printString("UPC-A\n")
                    .printBarCode("123456789012", POSConst.BCS_UPCA)
                    .printString("UPC-E\n")
                    .printBarCode("042100005264", POSConst.BCS_UPCE, 2, 70, POSConst.ALIGNMENT_LEFT)
                    .printString("JAN8\n")
                    .printBarCode("12345678", POSConst.BCS_JAN8, 2, 70, POSConst.ALIGNMENT_CENTER)
                    .printString("JAN13\n")
                    .printBarCode("123456791234", POSConst.BCS_JAN13, 2, 70, POSConst.ALIGNMENT_RIGHT)
                    .printString("CODE39\n")
                    .printBarCode(
                        "ABCDEFGHI",
                        POSConst.BCS_Code39,
                        2,
                        70,
                        POSConst.ALIGNMENT_CENTER,
                        POSConst.HRI_TEXT_BOTH
                    )
                    .printString("ITF\n")
                    .printBarCode("123456789012", POSConst.BCS_ITF, 70)
                    .printString("CODABAR\n")
                    .printBarCode("A37859B", POSConst.BCS_Codabar, 70)
                    .printString("CODE93\n")
                    .printBarCode("123456789", POSConst.BCS_Code93, 70)
                    .printString("CODE128\n")
                    .printBarCode("{BNo.123456", POSConst.BCS_Code128, 2, 70, POSConst.ALIGNMENT_LEFT)
                    .feedLine()
                    .cutHalfAndFeed(1)

                _toastMessage.value = "Barcode printed successfully"
            } catch (e: Exception) {
                _toastMessage.value = "Print failed: ${e.message}"
            }
        }
    }

    fun printPicture(bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                printer.initializePrinter()
                    .printBitmap(bitmap, POSConst.ALIGNMENT_CENTER, 384)
                    .feedLine()
                    .cutHalfAndFeed(1)

                _toastMessage.value = "Picture printed successfully"
            } catch (e: Exception) {
                _toastMessage.value = "Print failed: ${e.message}"
            }
        }
    }

    fun printQRCode() {
        viewModelScope.launch {
            try {
                val content = "Welcome to Printer Technology to create advantages Quality to win in the future"
                printer.initializePrinter()
                    .printQRCode(content)
                    .feedLine()
                    .cutHalfAndFeed(1)

                _toastMessage.value = "QR Code printed successfully"
            } catch (e: Exception) {
                _toastMessage.value = "Print failed: ${e.message}"
            }
        }
    }

    fun checkConnection() {
        viewModelScope.launch {
            val status = if (deviceConnection.isConnect) {
                "Connected"
            } else {
                "Disconnected"
            }
            _toastMessage.value = status
        }
    }

    fun getPrinterStatus() {
        viewModelScope.launch {
            try {
                printer.printerStatus { status ->
                    val msg = when (status) {
                        POSConst.STS_NORMAL -> "Printer Normal"
                        POSConst.STS_COVEROPEN -> "Front Cover Open"
                        POSConst.STS_PAPEREMPTY -> "Out of Paper"
                        POSConst.STS_PRESS_FEED -> "Press Feed"
                        POSConst.STS_PRINTER_ERR -> "Printer Error"
                        else -> "Unknown Status"
                    }
                    _toastMessage.value = "Printer Status: $msg"
                }
            } catch (e: Exception) {
                _toastMessage.value = "Status check failed: ${e.message}"
            }
        }
    }

    fun openCashDrawer() {
        viewModelScope.launch {
            try {
                printer.openCashBox(POSConst.PIN_TWO)
                _toastMessage.value = "Cash drawer opened"
            } catch (e: Exception) {
                _toastMessage.value = "Failed to open drawer: ${e.message}"
            }
        }
    }

    fun printTable() {
        viewModelScope.launch {
            try {
                val table = PTable(
                    arrayOf("Item", "QTY", "Price", "Total"),
                    arrayOf(13, 10, 10, 11),
                    arrayOf(0, 0, 1, 1)
                )
                    .addRow("Apple Apple xxxxxxxxxxxxx", arrayOf("100328", "1", "7.99", "7.99"), "remarks:xxxxxxxx")
                    .addRow("680015", "4", "0.99", "3.96")
                    .addRow("102501102501102501", "1", "43.99", "43.99")
                    .addRow("021048", "1", "4.99", "4.99")

                printer.initializePrinter()
                    .printTable(table)
                    .feedLine(2)
                    .cutHalfAndFeed(1)

                _toastMessage.value = "Table printed successfully"
            } catch (e: Exception) {
                _toastMessage.value = "Print failed: ${e.message}"
            }
        }
    }

    fun showWifiConfig() {
        _toastMessage.value = "WiFi Config - Feature coming soon"
    }

    fun showNetConfig() {
        _toastMessage.value = "Network Config - Feature coming soon"
    }

    fun showBleConfig() {
        _toastMessage.value = "Bluetooth Config - Feature coming soon"
    }

    fun querySerialNumber() {
        viewModelScope.launch {
            try {
                printer.getSerialNumber { serialBytes ->
                    val serialNumber = String(serialBytes, Charset.defaultCharset())
                    _toastMessage.value = "Serial Number: $serialNumber"
                }
            } catch (e: Exception) {
                _toastMessage.value = "Query failed: ${e.message}"
            }
        }
    }

    fun setIpViaUdp() {
        _toastMessage.value = "Set IP via UDP - Feature coming soon"
    }

    fun selectBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                printer.selectBitmapModel(POSConst.SINGLE_DENSITY_8, 100, bitmap)
                    .feedLine(5)

                _toastMessage.value = "Bitmap printed successfully"
            } catch (e: Exception) {
                _toastMessage.value = "Print failed: ${e.message}"
            }
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}