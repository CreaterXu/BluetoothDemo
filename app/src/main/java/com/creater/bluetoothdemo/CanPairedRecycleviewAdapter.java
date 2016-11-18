package com.creater.bluetoothdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Utf8;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;



/**
 * Created by Administrator on 2016/11/17.
 */

public class CanPairedRecycleviewAdapter extends RecyclerView.Adapter<CanPairedRecycleviewAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<BluetoothDevice> mDevices;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mClientSocket;

    public CanPairedRecycleviewAdapter(Context mContext, ArrayList<BluetoothDevice> devices, BluetoothAdapter adapter) {
        this.mContext = mContext;
        if (devices == null) {
            Log.e("xv", "devices is null");
        }
        this.mDevices = devices;
        this.mBluetoothAdapter = adapter;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_devices, null, false));
        return myViewHolder;
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (mDevices == null) {
            Log.e("xv", "mdevices is null");
        }
        holder.deviceNameTextView.setText(mDevices.get(position).getName() + "---" + mDevices.get(position).getAddress() + "===" + mDevices.get(position).getUuids());
        holder.deviceNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //蓝牙配对
                Snackbar.make(holder.deviceNameTextView, "进入匹配", 5000).show();
                startPairing(mBluetoothAdapter, position);
            }
        });
    }

    private void startPairing(BluetoothAdapter adapter, int position) {

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        BluetoothDevice device = adapter.getRemoteDevice(mDevices.get(position).getAddress());

        try {
            if (mClientSocket == null) {
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    //利用反射方法调用BluetoothDevice.createBond(BluetoothDevice remoteDevice);
                    Method createBondMethod = BluetoothDevice.class
                            .getMethod("createBond");
                    //BluetoothUtils.createBond(BluetoothDevice.class,device);
                    Log.e("BlueToothTestActivity", "开始配对"+createBondMethod.getName());
                    boolean pairing=(Boolean) createBondMethod.invoke(device);

                }

            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /******************
     * viewholder
     ************************/
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView deviceNameTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            deviceNameTextView = (TextView) itemView.findViewById(R.id.device_name_txt);
        }
    }
}
