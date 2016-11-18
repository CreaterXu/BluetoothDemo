package com.creater.bluetoothserver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by Administrator on 2016/11/17.
 */

public class ServerSocket extends Thread {
    private BluetoothAdapter mBluetoothAdapter;

    InputStream mInputStream;
    BluetoothServerSocket mBluetoothServerSocket;
    BluetoothSocket mBluetoothSocket;

    private Context mContext;
    private final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final String NAME = "Bluetooth_Socket";


    public ServerSocket(Context mContext, BluetoothAdapter bluetoothAdapter) {
        this.mContext = mContext;
        try {
            mBluetoothAdapter=bluetoothAdapter;
            mBluetoothServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (Exception e) {
        }
    }

    @Override
    public void run() {
        try {
            mBluetoothSocket=mBluetoothServerSocket.accept();
            mInputStream=mBluetoothSocket.getInputStream();
            while(true) {
                byte[] buffer =new byte[1024];
                int count = mInputStream.read(buffer);
                Message msg = new Message();
                msg.obj = new String(buffer, 0, count, "utf-8");
                handler.sendMessage(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Toast.makeText(mContext, String.valueOf(msg.obj),
                    Toast.LENGTH_LONG).show();
            super.handleMessage(msg);
        }
    };
}
