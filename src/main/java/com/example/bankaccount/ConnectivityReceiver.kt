package com.example.bankaccount

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi


class ConnectivityReceiver: BroadcastReceiver() {

    public var isConnected = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context, arg1: Intent) {
        isConnected = isConnectedOrConnecting(context)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    public fun isConnectedOrConnecting(context: Context): Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val currentNetwork = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(currentNetwork)

        if(caps != null){
            Toast.makeText(context, R.string.connected, Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(context, R.string.disconnected, Toast.LENGTH_SHORT).show()
        }

        return caps != null
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }
}