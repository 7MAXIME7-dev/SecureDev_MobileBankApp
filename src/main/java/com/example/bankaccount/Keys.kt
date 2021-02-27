package com.example.bankaccount

object Keys {

    init {
        System.loadLibrary("native-lib")
    }

    external fun apiConfigKey(): String
    external fun apiAccountKey(): String
}


