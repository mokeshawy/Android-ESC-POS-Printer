package com.example.esc_pos_printer

import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.emh.thermalprinter.EscPosPrinter
import com.emh.thermalprinter.connection.bluetooth.BluetoothPrintersConnections
import com.emh.thermalprinter.exceptions.EscPosBarcodeException
import com.emh.thermalprinter.exceptions.EscPosConnectionException
import com.emh.thermalprinter.exceptions.EscPosEncodingException
import com.emh.thermalprinter.exceptions.EscPosParserException
import com.emh.thermalprinter.textparser.PrinterTextParserImg
import com.example.esc_pos_printer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    private var PERMISSION_BLUETOOTH = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBluetooth.setOnClickListener {
            printBluetooth()
        }

    }

    /**
     * ------------ BLUETOOTH PART -------------- *
     */
    @Suppress("DEPRECATION")
    private fun printBluetooth() {
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.BLUETOOTH),PERMISSION_BLUETOOTH)
        }else{
            var printer : EscPosPrinter? = null
            try {
                printer = EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(),203, 48f, 32)
                printer.printFormattedTextAndCut(
                    "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, resources.getDrawableForDensity(R.drawable.github_logo, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
                            "[L]\n" +
                            "[C]<u><font size='big'>ORDER N°1125</font></u>\n[L]\n" +
                            "[L] _________________________________________\n" +
                            "[L] Description [R]Amount\n[L]\n" +
                            "[L] <b>Beef Burger [R]10.00\n" +
                            "[L] Sprite-200ml [R]3.00\n" +
                            "[L] _________________________________________\n" +
                            "[L] TOTAL [R]13.00 BD\n" +
                            "[L] Total Vat Collected [R]1.00 BD\n" +
                            "[L]\n" +
                            "[L] _________________________________________\n" +
                            "[L]\n" +
                            "[C]<font size='tall'>Customer Info</font>\n" +
                            "[L] EM Haseeb\n" +
                            "[L] 14 Streets\n" +
                            "[L] Cantt, LHR\n" +
                            "[L] Tel : +923040017916\n" +
                            "[L]\n" +
                            "[L] <barcode type='ean13' height='10'>831254784551</barcode>\n[L]\n" +
                            "[L] <qrcode>http://github.com/EmHaseeb/</qrcode>\n[L]\n[L]\n[L]\n"
                )
            }catch ( e: EscPosConnectionException) {
                e.printStackTrace();
                 AlertDialog.Builder(this)
                    .setTitle("Broken connection")
                    .setMessage(e.message)
                    .show();
            } catch ( e: EscPosParserException) {
                e.printStackTrace();
                AlertDialog.Builder(this)
                    .setTitle("Invalid formatted text")
                    .setMessage(e.message)
                    .show();
            } catch (e: EscPosEncodingException) {
                e.printStackTrace();
                AlertDialog.Builder(this)
                    .setTitle("Bad selected encoding")
                    .setMessage(e.message)
                    .show();
            } catch (e: EscPosBarcodeException) {
                e.printStackTrace();
                AlertDialog.Builder(this)
                    .setTitle("Invalid barcode")
                    .setMessage(e.message)
                    .show();
            }
        }
    }

    override fun onRequestPermissionsResult( requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            when(requestCode){
                PERMISSION_BLUETOOTH ->{
                    printBluetooth()
                }
            }
        }
    }
}