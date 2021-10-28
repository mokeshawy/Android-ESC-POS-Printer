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