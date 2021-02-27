package com.example.bankaccount

import android.content.*
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.os.StrictMode
import android.util.Base64
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL


class AccountConfig : AppCompatActivity() {



    private var receiver = ConnectivityReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_config)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        registerReceiver(receiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
    }


    private fun getSharedPreferences(sharedPrefsFile: String): SharedPreferences{
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)


        return EncryptedSharedPreferences.create(
                sharedPrefsFile,
                masterKey,
                applicationContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    private fun saveConfig(myId: Int){

        val config = getConfig(myId)


        if(config != null){

            val sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefsConfig))

            with(sharedPreferences.edit()) {
                val id = config.getInt("id")
                val name = config.getString("name")
                val lastname = config.getString("lastname")

                this.putInt("id", id)
                this.putString("name", name)
                this.putString("lastname", lastname)

                this.apply()
            }
        }
    }

    private fun saveAccounts(){

        val accounts = getAccounts()

        if(accounts != null){
            val sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefsAccount))

            with(sharedPreferences.edit()) {
                this.putString(getString(R.string.accountsKey), accounts.toString())
                this.apply()
            }
        }
    }


    private fun getAccounts(): JSONArray? {

        val url = URL(Keys.apiAccountKey())
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        var json: JSONArray? = null

        try{
            // Get status code
            val status = urlConnection.responseCode

            // get Data
            val data = urlConnection.inputStream.bufferedReader().readText()
            json = JSONArray(data)

            return json
        }
        catch (e: IOException){
            e.printStackTrace()
        }
        finally{
            urlConnection.disconnect()
        }

        return json
    }

    private fun getConfig(myId: Int): JSONObject? {

        val url = URL(Keys.apiConfigKey() + myId)
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        var json: JSONObject? = null

        try{
            // Get status code
            val status = urlConnection.responseCode

            // get Data
            val data = urlConnection.inputStream.bufferedReader().readText()
            json = JSONObject(data)

            return json
        }
        catch (e: IOException){
            e.printStackTrace()
        }
        finally{
            urlConnection.disconnect()
        }

        return json
    }


    fun enrollUser(view: View){

        // get error TextView object
        val errorTextView: TextView = findViewById(R.id.TextViewId)

        val myEditText: EditText = findViewById(R.id.editTextId)
        val myId = myEditText.text.toString()


        if(myId != "" && myId.toInt() > 0 && receiver.isConnected)
        {

            saveConfig(myId.toInt())
            saveAccounts()

            errorTextView.text = ""

            val intent = Intent(this, MyAccount::class.java)
            startActivity(intent)

            this.finish()

        }
        else{
            if(!receiver.isConnected) errorTextView.text = getString(R.string.network_issues)
            else errorTextView.text = getString(R.string.invalid_field)
        }
    }


}