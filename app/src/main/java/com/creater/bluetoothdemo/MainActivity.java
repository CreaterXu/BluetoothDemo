package com.creater.bluetoothdemo;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.start_button)
    Button startButton;
    @BindView(R.id.start_bundle_button)
    Button startBundleButton;
    @BindView(R.id.stop_button)
    Button stopButton;
    @BindView(R.id.can_paried_device_textview)
    TextView canPariedDeviceTextview;

    BluetoothAdapter bluetoothAdapter;
    StringBuilder pairedDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        startButton.setOnClickListener(this);
        startBundleButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        //请求权限在6.0中有用
        requestBluetoothPermission();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //每搜索到一个设备就会发送一个该广播
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(receiver, filter);
        //当全部搜索完后发送该广播
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(receiver, filter);
        pairedDevices = new StringBuilder();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start_button) {
            startBluetooth();
        } else if (v.getId() == R.id.stop_button) {
            stopBluetooth();
        } else if (v.getId() == R.id.start_bundle_button) {
            startPairing();
        } else if (v.getId() == R.id.start_bundle_button) {
            startSearchBluetooth();
        }
    }

    private void startPairing() {
        Set<BluetoothDevice> pairedDeviceList = bluetoothAdapter.getBondedDevices();

        for (BluetoothDevice bluetoothDevice :
                pairedDeviceList) {
            pairedDevices.append("++");
            pairedDevices.append(bluetoothDevice.getName());
        }
        canPariedDeviceTextview.setText(pairedDevices.toString());
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


    /**
     * 定义广播接收器
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    pairedDevices.append(device.getName() + ":" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //已搜素完成
                canPariedDeviceTextview.setText(pairedDevices);
            }
        }
    };
}
