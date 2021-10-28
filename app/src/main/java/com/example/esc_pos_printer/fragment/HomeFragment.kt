package com.example.esc_pos_printer.fragment

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.example.esc_pos_printer.databinding.FragmentHomeBinding
import com.example.esc_pos_printer.fragment.HomeFragmentViewModel.Companion.PERMISSION_BLUETOOTH


class HomeFragment : Fragment() {

    lateinit var binding : FragmentHomeBinding
    private val homeFragmentViewModel : HomeFragmentViewModel by viewModels()
    var selectDevice : BluetoothConnection? = null
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
            browseBluetoothDevice()
        }

        /* ----- btn print with bluetooth ---- */
        binding.btnBluetooth.setOnClickListener {
            homeFragmentViewModel.printBluetooth(requireActivity())
        }
    }



    /**
     * ------------ BLUETOOTH PART -------------- *
     */
    private fun browseBluetoothDevice() {
        val bluetoothDevicesList = BluetoothPrintersConnections().list
        if(bluetoothDevicesList != null ){
            val items = arrayOfNulls<String>(bluetoothDevicesList.size + 1)
            items[0] = "Default printer"
            var i = 0
            for( device in bluetoothDevicesList){
                items[++i] = device.device.name
            }
            val dialog = AlertDialog.Builder(requireActivity())
            dialog.setTitle("Bluetooth printer selection")
            dialog.setItems(items){dialog, which ->
                val index = i - 1
                if( index == -1 ){
                    selectDevice = null
                }else{
                    selectDevice = bluetoothDevicesList[index]
                }
                binding.btnBluetoothBrowsePrinter.text = items[i]
            }
            dialog.create()
            dialog.setCancelable(false)
            dialog.show()
        }
    }


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