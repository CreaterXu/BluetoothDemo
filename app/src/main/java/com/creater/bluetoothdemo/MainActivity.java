package com.creater.bluetoothdemo;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.start_button)
    Button startButton;

    @BindView(R.id.stop_button)
    Button stopButton;
    @BindView(R.id.can_paried_device_textview)
    TextView canPariedDeviceTextview;

    BluetoothAdapter bluetoothAdapter;
    StringBuilder pairedDevices;
    CanPairedRecycleviewAdapter canPairedRecycleviewAdapter;
    ArrayList<BluetoothDevice> mDevices;
    boolean haveDevice = false;

    @BindView(R.id.start_search_button)
    Button startSearchButton;
    @BindView(R.id.can_paried_device_recyclistview)
    RecyclerView canPariedDeviceRecyclistview;


    ServerSocket mServer;
    @BindView(R.id.make_showed_togglebutton)
    ToggleButton makeShowedTogglebutton;
    @BindView(R.id.send_msg_button)
    Button sendMsgButton;
    @BindView(R.id.activity_main)
    RelativeLayout activityMain;

    private BluetoothDevice device;
    private BluetoothSocket mClientSocket;
    private final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        startSearchButton.setOnClickListener(this);
        //请求权限在6.0中有用
        requestBluetoothPermission();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //每搜索到一个设备就会发送一个该广播
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(receiver, filter);
        //当全部搜索完后发送该广播
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(receiver, filter);
        pairedDevices = new StringBuilder();
        mDevices = new ArrayList<>();
        canPariedDeviceRecyclistview.setLayoutManager(new LinearLayoutManager(this));
        canPairedRecycleviewAdapter = new CanPairedRecycleviewAdapter(MainActivity.this, mDevices, bluetoothAdapter);
        canPariedDeviceRecyclistview.setAdapter(canPairedRecycleviewAdapter);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start_button) {
            startBluetooth();
        } else if (v.getId() == R.id.stop_button) {
            stopBluetooth();
        } else if (v.getId() == R.id.start_search_button) {
            startSearchBluetooth();
        }
    }


    @OnClick(R.id.make_showed_togglebutton)
    public void showOurBluetooth() {
        if (makeShowedTogglebutton.getText() == makeShowedTogglebutton.getTextOff()) {
            Intent sIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            sIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
            startActivityForResult(sIntent, 2);
        } else {
            Intent sIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(sIntent, 1);
        }
    }

    private void startBluetooth() {
        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
    }

    private void stopBluetooth() {
        bluetoothAdapter.disable();
    }

    /**
     * 搜索蓝牙设备
     */
    private void startSearchBluetooth() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                Toast.makeText(this, "start bluetooth success", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(this, "一小时内其他设备可以搜索到此设备", Toast.LENGTH_SHORT).show();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static final int REQUEST_BLUETOOTH_PERMISSION = 10;

    private void requestBluetoothPermission() {
        //判断系统版本
        if (Build.VERSION.SDK_INT >= 23) {
            //检测当前app是否拥有某个权限
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            //判断这个权限是否已经授权过
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                //判断是否需要 向用户解释，为什么要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))
                    Toast.makeText(this, "Need bluetooth permission.", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_BLUETOOTH_PERMISSION);
                return;
            } else {
            }
        } else {
        }
    }

    @OnClick(R.id.send_msg_button)
    public void sendMessage(){
        try {
            mClientSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            mClientSocket.connect();
            outputStream = mClientSocket.getOutputStream();
            if (outputStream != null) {
                try {
                    outputStream.write("first bluetooth message".getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 定义广播接收器
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("xv", "接收到广播");
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    for (BluetoothDevice dev : mDevices) {
                        if (dev.getName().equals(device.getName())) {
                            haveDevice = true;
                            break;
                        }
                    }
                    if (!haveDevice) {
                        pairedDevices.append(device.getName() + ":" + device.getAddress() + "---");
                        mDevices.add(device);
                    }

                }
                canPairedRecycleviewAdapter.notifyDataSetChanged();
                canPariedDeviceTextview.setText(pairedDevices);
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        Log.d("BlueToothTestActivity", "正在配对......");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.d("BlueToothTestActivity", "完成配对");
                        sendMsgButton.setVisibility(View.VISIBLE);
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Log.d("BlueToothTestActivity", "取消配对");
                    default:
                        break;
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //已搜素完
                canPairedRecycleviewAdapter.notifyDataSetChanged();
                startSearchButton.setText("search end");
                canPariedDeviceTextview.setText(pairedDevices);
            }
        }
    };


}
