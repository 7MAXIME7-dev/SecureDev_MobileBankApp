package com.example.bankaccount

import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.File


class MainActivity : AppCompatActivity() {

    private var cancellationSignal: CancellationSignal? = null

    private var fileName = "CONFIG.xml"
    private var file = File("/data/data/com.example.bankaccount/shared_prefs/", fileName)

    private val authenticationCallback: BiometricPrompt.AuthenticationCallback

    get() =
        @RequiresApi(Build.VERSION_CODES.P)
        object : BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationError(errorCode:Int, errString:CharSequence?){
                super.onAuthenticationError(errorCode, errString)
                notifyUser("Authentication error: $errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                notifyUser("Access Granted!")

                startConfigActivity()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkBiometricSupport()
    }

    fun startConfigActivity(){

        if(file.exists()){

            val intent = Intent(this, MyAccount::class.java)
            startActivity(intent)

        } else {

            val intent = Intent(this, AccountConfig::class.java)
            startActivity(intent)
        }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    public fun authenticate(view: View){
        val biometricPrompt = BiometricPrompt.Builder(this)
                .setTitle("Authenticate to your Bank Account")
                .setSubtitle("Access restricted")
                .setDescription("This app uses fingerprint protection to keep your account secure")
                .setNegativeButton("cancel", this.mainExecutor, DialogInterface.OnClickListener {
                    dialog, which -> notifyUser("Authentication cancelled")

                }).build()
        biometricPrompt.authenticate(getCancellationSignal(), mainExecutor, authenticationCallback)
    }


    private fun getCancellationSignal(): CancellationSignal{
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Authentication was cancelled by the user")
        }
        return cancellationSignal as CancellationSignal
    }

    private fun notifyUser(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun checkBiometricSupport(): Boolean {
        val keyguardManager: KeyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if(!keyguardManager.isKeyguardSecure){
            notifyUser("Fingerprint authentication has not been enabled in settings")
            return false
        }

        if(ActivityCompat.checkSelfPermission(
                        this, android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED){

            notifyUser("Fingerprint authentication permission is not enabled")
            return false
        }

        return if(packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)){
            true
        }else true

    }



    public fun reset(view: View){

        if(file.exists()){

            file.delete()
            notifyUser("Reset Done !")
        }
        else{
            notifyUser("Already Reset !")
        }
    }






















}