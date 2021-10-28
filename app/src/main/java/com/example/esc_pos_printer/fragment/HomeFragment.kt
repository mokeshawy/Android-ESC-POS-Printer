package com.example.esc_pos_printer.fragment

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections
import com.example.esc_pos_printer.databinding.FragmentHomeBinding
import com.example.esc_pos_printer.fragment.HomeFragmentViewModel.Companion.ACTION_USB_PERMISSION
import com.example.esc_pos_printer.fragment.HomeFragmentViewModel.Companion.PERMISSION_BLUETOOTH


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

        /* ----- btn bowers bluetooth printer ----- */
        binding.btnBluetoothBrowsePrinter.setOnClickListener {
            homeFragmentViewModel.browseBluetoothDevice(requireActivity() ,binding.btnBluetoothBrowsePrinter)
        }

        /* ----- btn print with bluetooth ---- */
        binding.btnBluetooth.setOnClickListener {
            homeFragmentViewModel.printBluetooth(requireActivity())
        }

        /* ------ btn print with USB -------- */
        binding.btnUsb.setOnClickListener {
            printUsb()
        }
    }


    /**
     * ------------------- USB Part ----------------
     */

    private fun printUsb(){
        val usbConnection = UsbPrintersConnections.selectFirstConnected(requireActivity())
        val usbManager = requireActivity().getSystemService(Context.USB_SERVICE) as UsbManager
        if(usbConnection == null || usbManager == null ){
            val dialog = AlertDialog.Builder(requireActivity())
            dialog.setTitle("USB Connection")
            dialog.setMessage("No USB printer found.")
            dialog.show()
            return
        }
        val permissionIntent = PendingIntent.getBroadcast(requireActivity(),0,Intent(ACTION_USB_PERMISSION),0)
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        requireActivity().registerReceiver(usbReceiver,filter)
        usbManager.requestPermission(usbConnection.device,permissionIntent)
    }

    private val usbReceiver = object : BroadcastReceiver(){
        override fun onReceive(context : Context?, intent : Intent?) {
            val action = intent!!.action
            if(ACTION_USB_PERMISSION == action){
                synchronized(requireActivity()){
                    val usbManager = requireActivity().getSystemService(Context.USB_SERVICE) as UsbManager?
                    val usbDevice = intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                    if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)){
                        if(usbManager !=null && usbDevice != null ){
                            homeFragmentViewModel.printUsbPart(requireActivity() , usbManager , usbDevice)
                        }
                    }
                }
            }
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