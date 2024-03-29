package com.example.esc_pos_printer.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.DisplayMetrics
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.connection.tcp.TcpConnection
import com.dantsu.escposprinter.connection.usb.UsbConnection
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.dantsu.escposprinter.exceptions.EscPosEncodingException
import com.dantsu.escposprinter.exceptions.EscPosParserException
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.example.esc_pos_printer.R
import java.text.SimpleDateFormat
import java.util.*

class HomeFragmentViewModel : ViewModel() {

    companion object{
        /* ----- bluetooth permission number ----- */
        var PERMISSION_BLUETOOTH = 1

        /* ------ usb permission ----*/
        var ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
    }


    /**
     * ------------ BLUETOOTH PART -------------- *
     */
    private var selectDevice : BluetoothConnection? = null

    /* ------------- select printer device --------- */
    fun browseBluetoothDevice( context: Context , btnBluetoothBrowsPrinter : Button ) {
        val bluetoothDevicesList = BluetoothPrintersConnections().list
        if(bluetoothDevicesList != null ){
            val items = arrayOfNulls<String>(bluetoothDevicesList.size + 1)
            items[0] = "Default printer"
            var i = 0

            for( device in bluetoothDevicesList){
                items[++i] = device.device.name
            }
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle("Bluetooth printer selection")
            dialog.setItems(items){dialog, which ->
                val index = which - 1
                if( index == -1 ){
                    selectDevice = null
                }else{
                    selectDevice = bluetoothDevicesList[index]
                }
                btnBluetoothBrowsPrinter.text = items[which]
            }
            dialog.create()
            dialog.setCancelable(false)
            dialog.show()
        }
    }

    fun printBluetooth(context: Context) {
        if(ContextCompat.checkSelfPermission(context,android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.BLUETOOTH),PERMISSION_BLUETOOTH)
        }else{
            val format = SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss")
            var printer : EscPosPrinter? = null
            try {
                printer = EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(),203, 48f, 32)
                @Suppress("DEPRECATION")
                printer.printFormattedTextAndCut(
                    "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, context.resources.getDrawableForDensity(
                        R.drawable.github_logo, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
                            "[L]\n" +
                            "[C]<u><font size='big'>Mohamed Keshawy</font></u>\n" +
                            "[L]\n" +
                            "[C]<u type='double'>" + format.format(Date()) + "</u>\n" +
                            "[C]\n" +
                            "[C]================================\n" +
                            "[L]\n" +
                            "[L]<b>BEAUTIFUL SHIRT</b>[R]9.99€\n" +
                            "[L]  + Size : S\n" +
                            "[L]\n" +
                            "[L]<b>AWESOME HAT</b>[R]24.99€\n" +
                            "[L]  + Size : 57/58\n" +
                            "[L]\n" +
                            "[C]--------------------------------\n" +
                            "[R]TOTAL PRICE :[R]34.98€\n" +
                            "[R]TAX :[R]4.23€\n" +
                            "[L]\n" +
                            "[C]================================\n" +
                            "[L]\n" +
                            "[C]<font size='tall'>Customer Info</font>\n" +
                            "[L] M KESHAWY\n" +
                            "[L] 14 Streets\n" +
                            "[L] Cairo, Egypt\n" +
                            "[L] Tel : +201007169654\n" +
                            "\n" +
                            "[C]<barcode type='ean13' height='10'>831254784551</barcode>\n" +
                            "[L]\n" +
                            "[C]<qrcode size='20'>https://github.com/mokeshawy/</qrcode>\n"
                )
            }catch ( e: EscPosConnectionException) {
                e.printStackTrace();
                AlertDialog.Builder(context)
                    .setTitle("Broken connection")
                    .setMessage(e.message)
                    .show();
            } catch ( e: EscPosParserException) {
                e.printStackTrace();
                AlertDialog.Builder(context)
                    .setTitle("Invalid formatted text")
                    .setMessage(e.message)
                    .show();
            } catch (e: EscPosEncodingException) {
                e.printStackTrace();
                AlertDialog.Builder(context)
                    .setTitle("Bad selected encoding")
                    .setMessage(e.message)
                    .show();
            } catch (e: EscPosBarcodeException) {
                e.printStackTrace();
                AlertDialog.Builder(context)
                    .setTitle("Invalid barcode")
                    .setMessage(e.message)
                    .show();
            }
        }
    }



    /**
     * ------------------- USB Part ----------------
     */
    fun printUsbPart( context: Context , usbManager: UsbManager , usbDevice: UsbDevice) {
        if(ContextCompat.checkSelfPermission(context,android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.BLUETOOTH),PERMISSION_BLUETOOTH)
        }else{
            val format = SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss")
            var printer : EscPosPrinter? = null
            try {

                printer = EscPosPrinter(UsbConnection(usbManager, usbDevice),203, 48f, 32)
                @Suppress("DEPRECATION")
                printer.printFormattedTextAndCut(
                    "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, context.resources.getDrawableForDensity(
                        R.drawable.github_logo, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
                            "[L]\n" +
                            "[C]<u><font size='big'>Mohamed Keshawy</font></u>\n" +
                            "[L]\n" +
                            "[C]<u type='double'>" + format.format(Date()) + "</u>\n" +
                            "[C]\n" +
                            "[C]================================\n" +
                            "[L]\n" +
                            "[L]<b>BEAUTIFUL SHIRT</b>[R]9.99€\n" +
                            "[L]  + Size : S\n" +
                            "[L]\n" +
                            "[L]<b>AWESOME HAT</b>[R]24.99€\n" +
                            "[L]  + Size : 57/58\n" +
                            "[L]\n" +
                            "[C]--------------------------------\n" +
                            "[R]TOTAL PRICE :[R]34.98€\n" +
                            "[R]TAX :[R]4.23€\n" +
                            "[L]\n" +
                            "[C]================================\n" +
                            "[L]\n" +
                            "[C]<font size='tall'>Customer Info</font>\n" +
                            "[L] M KESHAWY\n" +
                            "[L] 14 Streets\n" +
                            "[L] Cairo, Egypt\n" +
                            "[L] Tel : +201007169654\n" +
                            "\n" +
                            "[C]<barcode type='ean13' height='10'>831254784551</barcode>\n" +
                            "[L]\n" +
                            "[C]<qrcode size='20'>https://github.com/mokeshawy/</qrcode>\n"
                )
            }catch ( e: EscPosConnectionException) {
                e.printStackTrace();
                AlertDialog.Builder(context)
                    .setTitle("Broken connection")
                    .setMessage(e.message)
                    .show();
            } catch ( e: EscPosParserException) {
                e.printStackTrace();
                AlertDialog.Builder(context)
                    .setTitle("Invalid formatted text")
                    .setMessage(e.message)
                    .show();
            } catch (e: EscPosEncodingException) {
                e.printStackTrace();
                AlertDialog.Builder(context)
                    .setTitle("Bad selected encoding")
                    .setMessage(e.message)
                    .show();
            } catch (e: EscPosBarcodeException) {
                e.printStackTrace();
                AlertDialog.Builder(context)
                    .setTitle("Invalid barcode")
                    .setMessage(e.message)
                    .show();
            }
        }
    }

    /**
     * ------------------- WIFI Part ----------------
     */
    fun printWifiPart( context: Context , ip : String , port : Int ) {
        if(ContextCompat.checkSelfPermission(context,android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.BLUETOOTH),PERMISSION_BLUETOOTH)
        }else{
            val format = SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss")

            try {
               val printer = EscPosPrinter(TcpConnection(ip,port),203, 48f, 32)
                @Suppress("DEPRECATION")
                printer.printFormattedTextAndCut(
                    "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, context.resources.getDrawableForDensity(
                        R.drawable.github_logo, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
                            "[L]\n" +
                            "[C]<u><font size='big'>Mohamed Keshawy</font></u>\n" +
                            "[L]\n" +
                            "[C]<u type='double'>" + format.format(Date()) + "</u>\n" +
                            "[C]\n" +
                            "[C]================================\n" +
                            "[L]\n" +
                            "[L]<b>BEAUTIFUL SHIRT</b>[R]9.99€\n" +
                            "[L]  + Size : S\n" +
                            "[L]\n" +
                            "[L]<b>AWESOME HAT</b>[R]24.99€\n" +
                            "[L]  + Size : 57/58\n" +
                            "[L]\n" +
                            "[C]--------------------------------\n" +
                            "[R]TOTAL PRICE :[R]34.98€\n" +
                            "[R]TAX :[R]4.23€\n" +
                            "[L]\n" +
                            "[C]================================\n" +
                            "[L]\n" +
                            "[C]<font size='tall'>Customer Info</font>\n" +
                            "[L] M KESHAWY\n" +
                            "[L] 14 Streets\n" +
                            "[L] Cairo, Egypt\n" +
                            "[L] Tel : +201007169654\n" +
                            "\n" +
                            "[C]<barcode type='ean13' height='10'>831254784551</barcode>\n" +
                            "[L]\n" +
                            "[C]<qrcode size='20'>https://github.com/mokeshawy/</qrcode>\n"
                )
            }catch ( e: EscPosConnectionException) {
                e.printStackTrace();
                AlertDialog.Builder(context)
                    .setTitle("Broken connection")
                    .setMessage(e.message)
                    .show();
            } catch ( e: EscPosParserException) {
                e.printStackTrace();
                AlertDialog.Builder(context)
                    .setTitle("Invalid formatted text")
                    .setMessage(e.message)
                    .show();
            } catch (e: EscPosEncodingException) {
                e.printStackTrace();
                AlertDialog.Builder(context)
                    .setTitle("Bad selected encoding")
                    .setMessage(e.message)
                    .show();
            } catch (e: EscPosBarcodeException) {
                e.printStackTrace();
                AlertDialog.Builder(context)
                    .setTitle("Invalid barcode")
                    .setMessage(e.message)
                    .show();
            }
        }
    }
}