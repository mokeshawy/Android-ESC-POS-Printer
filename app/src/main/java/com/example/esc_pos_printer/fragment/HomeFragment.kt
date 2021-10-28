package com.example.esc_pos_printer.fragment

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
import androidx.fragment.app.viewModels
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.dantsu.escposprinter.exceptions.EscPosEncodingException
import com.dantsu.escposprinter.exceptions.EscPosParserException
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.example.esc_pos_printer.R
import com.example.esc_pos_printer.databinding.FragmentHomeBinding
import com.example.esc_pos_printer.fragment.HomeFragmentViewModel.Companion.PERMISSION_BLUETOOTH
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    lateinit var binding : FragmentHomeBinding
    private val homeFragmentViewModel : HomeFragmentViewModel by viewModels()
    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* connect with view model */
        binding.lifecycleOwner = this
        binding.homeFragment = homeFragmentViewModel


        /* ----- btn print with bluetooth ---- */
        binding.btnBluetooth.setOnClickListener {
            homeFragmentViewModel.printBluetooth(requireActivity())
        }



    }

    /**
     * ------------ BLUETOOTH PART -------------- *
     */
    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult( requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            when(requestCode){
                PERMISSION_BLUETOOTH ->{
                    homeFragmentViewModel.printBluetooth(requireActivity())
                }
            }
        }
    }
}