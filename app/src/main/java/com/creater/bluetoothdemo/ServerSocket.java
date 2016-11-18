package com.creater.bluetoothdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Administrator on 2016/11/17.
 */

public class ServerSocket implements Runnable {
    private BluetoothAdapter mBluetoothAdapter;

    InputStream mInputStream;
    OutputStream mOutputStream;
    BluetoothServerSocket mBluetoothServerSocket;
    BluetoothSocket mBluetoothSocket;

    private Context mContext;

    public ServerSocket(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void run() {
        try {

            while (true) {
                mBluetoothServerSocket = mBluetoothAdapter
                        .listenUsingRfcommWithServiceRecord(
                                "btspp",
                                UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

                Log.i("服务端线程运行", "蓝牙服务端线程开始");

                Log.i("服务端线程运行", "蓝牙服务端线程阻塞中");

               mBluetoothSocket = mBluetoothServerSocket.accept();
                if (mBluetoothSocket != null) {
                    break;
                }

            }

            Log.i("服务端线程运行", "蓝牙服务端线程<<<<<<<<<<<<<");
            mInputStream = mBluetoothSocket.getInputStream();
            while(true) {
                byte[] buffer =new byte[1024];
                int count = mInputStream.read(buffer);
                Message msg = new Message();
                msg.obj = new String(buffer, 0, count, "utf-8");
                handler.sendMessage(msg);
            }


        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (mInputStream != null) {
                try {
                    mInputStream.close();
                    mInputStream = null;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            if (mInputStream != null) {
                try {
                    mOutputStream.close();
                    mOutputStream = null;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            if (mBluetoothSocket != null) {
                try {
                    mBluetoothSocket.close();
                    mBluetoothSocket = null;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            if (mBluetoothServerSocket != null) {
                try {
                    mBluetoothServerSocket.close();
                    mBluetoothServerSocket = null;
                    Looper.prepare();
                    Message message = Message.obtain();
                    message.what = 0x123456;
                    //mHandler.sendMessage(message);
                    Looper.loop();

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
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
