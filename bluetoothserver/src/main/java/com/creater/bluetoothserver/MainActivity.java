package com.creater.bluetoothserver;

import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private ServerSocket mServer;
    private BluetoothAdapter bluetoothAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //服务端线程监视
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        mServer = new ServerSocket(this, bluetoothAdapter);
        mServer.start();
    }
}
