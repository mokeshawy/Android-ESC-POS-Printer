package com.example.esc_pos_printer

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.dantsu.escposprinter.exceptions.EscPosEncodingException
import com.dantsu.escposprinter.exceptions.EscPosParserException
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.example.esc_pos_printer.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    lateinit var binding : FragmentHomeBinding
    private var PERMISSION_BLUETOOTH = 1
    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /* ----- btn print with bluetooth ---- */
        binding.btnBluetooth.setOnClickListener {
            printBluetooth()
        }
    }

    /**
     * ------------ BLUETOOTH PART -------------- *
     */

    private fun printBluetooth() {
        if(ContextCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.BLUETOOTH),PERMISSION_BLUETOOTH)
        }else{
            val format = SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss")
            var printer : EscPosPrinter? = null
            try {
                printer = EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(),203, 48f, 32)
                @Suppress("DEPRECATION")
                printer.printFormattedTextAndCut(
                    "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, resources.getDrawableForDensity(R.drawable.ic_solaurs_logo, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
                            "[L]\n" +
                            "[C]<u><font size='big'>MANEX</font></u>\n" +
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
                            "[L]<u><font color='bg-black' size='tall'>Customer :</font></u>\n" +
                            "[L]Raymond DUPONT\n" +
                            "[L]5 rue des girafes\n" +
                            "[L]31547 PERPETES\n" +
                            "[L]Tel : +33801201456\n" +
                            "\n" +
                            "[C]<barcode type='ean13' height='10'>831254784551</barcode>\n" +
                            "[L]\n" +
                            "[C]<qrcode size='20'>http://www.developpeur-web.dantsu.com/</qrcode>\n"
                )
            }catch ( e: EscPosConnectionException) {
                e.printStackTrace();
                AlertDialog.Builder(requireActivity())
                    .setTitle("Broken connection")
                    .setMessage(e.message)
                    .show();
            } catch ( e: EscPosParserException) {
                e.printStackTrace();
                AlertDialog.Builder(requireActivity())
                    .setTitle("Invalid formatted text")
                    .setMessage(e.message)
                    .show();
            } catch (e: EscPosEncodingException) {
                e.printStackTrace();
                AlertDialog.Builder(requireActivity())
                    .setTitle("Bad selected encoding")
                    .setMessage(e.message)
                    .show();
            } catch (e: EscPosBarcodeException) {
                e.printStackTrace();
                AlertDialog.Builder(requireActivity())
                    .setTitle("Invalid barcode")
                    .setMessage(e.message)
                    .show();
            }
        }
    }

    @Suppress("DEPRECATION")
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