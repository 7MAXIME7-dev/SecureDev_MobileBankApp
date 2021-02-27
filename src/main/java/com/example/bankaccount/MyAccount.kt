package com.example.bankaccount

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


data class Account(val id: String, val accountName: String, val amount: String, val iban: String, val currency: String){

    override fun toString(): String{
        return "Account Name: $accountName\n" +
                "Amount: $amount\n" +
                "IBAN: $iban\n" +
                "Currency: $currency\n"
    }
}



class MyAccount : AppCompatActivity() {

    private var fileName = "ACCOUNTS.xml"
    private var file = File("/data/data/com.example.bankaccount/shared_prefs/", fileName)

    private var receiver = ConnectivityReceiver()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_account)


        registerReceiver(receiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))

        val sharedPreferences1 = getSharedPreferences("CONFIG")
        val name = sharedPreferences1.getString("name", null)
        val lastname = sharedPreferences1.getString("lastname", null)

        val myNameView: TextView = findViewById(R.id.textViewName)

        myNameView.text = "Hello, $name $lastname"



        val myListAccount: ListView = findViewById(R.id.myListView)
        val myList = ArrayList<String>()

        val sharedPreferences2 = getSharedPreferences("ACCOUNTS")

        val jsonArrayAccount = JSONArray(sharedPreferences2.getString("accounts", null))

        for (i in 0 until jsonArrayAccount.length()) {
            val item = jsonArrayAccount.getJSONObject(i)

            val acc = Account(item.get("id").toString(), item.get("accountName").toString(),
                    item.get("amount").toString(), item.get("iban").toString(), item.get("currency").toString())

            myList.add(acc.toString())


        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, myList)
        myListAccount.adapter = adapter

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

    private fun saveAccounts(){

        val accounts = getAccounts()

        if(accounts != null){

            val sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefsAccount))

            with (sharedPreferences.edit()) {
                this.putString(getString(R.string.accountsKey), accounts.toString())
                this.apply()
            }
        }
    }

    private fun deleteOldAccounts(){
        if(file.exists()){
            file.delete()
        }
    }


    fun refresh(view: View){

        if(receiver.isConnected){
            deleteOldAccounts()
            saveAccounts()
            this.finish()
            val intent = Intent(this, MyAccount::class.java)
            startActivity(intent)
        }
    }


    private fun getSharedPreferences(sharedPrefsFile: String): SharedPreferences{
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        return EncryptedSharedPreferences.create(
                sharedPrefsFile,
                masterKey,
                applicationContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }




}