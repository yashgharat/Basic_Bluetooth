package com.example.basic_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private SwitchCompat bluetoothSwitch;
    private RecyclerView devicesList;
    private Button viewVisible, listDevices, findDevices;


    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothManager bluetoothManager;


    private ArrayList<String> deviceNameList, deviceAddressList;
    private List<BluetoothDevice> findDeviceList;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothSwitch = findViewById(R.id.bluetooth_switch);
        devicesList = findViewById(R.id.devices_list);
        listDevices = findViewById(R.id.list_devices);
        findDevices = findViewById(R.id.find_devices);
        viewVisible = findViewById(R.id.make_visible);
        swipeRefreshLayout = findViewById(R.id.swipe);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Device not supported", Toast.LENGTH_LONG).show();
            bluetoothSwitch.setVisibility(View.GONE);
        }

        bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        findDeviceList = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);



        if(bluetoothAdapter.isEnabled())
            bluetoothSwitch.setChecked(true);

        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    on();
                else
                    off();
            }
        });

        listDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list();
            }
        });

        viewVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visible();
            }
        });

        findDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MAIN", "devices: " + findDeviceList.size());
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }

    public void on() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            showToast("Turned On");
        } else {
            showToast("Already On");
        }
    }

    public void off() {
        bluetoothAdapter.disable();
        showToast("Turned off");
    }


    public void visible() {
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
    }


    public void list() {
        pairedDevices = bluetoothAdapter.getBondedDevices();

        deviceNameList = new ArrayList();
        deviceAddressList = new ArrayList();


        for (BluetoothDevice bt : pairedDevices) deviceNameList.add(bt.getName());
        for (BluetoothDevice bt : pairedDevices) deviceAddressList.add(bt.getAddress());


        showToast("Showing Paired Devices");

        final RecyclerView.Adapter adapter = new RecyclerView.Adapter<BluetoothViewHolder>() {
            @NonNull
            @Override
            public BluetoothViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View view = inflater.inflate(R.layout.device, parent, false);
                return new BluetoothViewHolder(view);            }

            @Override
            public void onBindViewHolder(@NonNull BluetoothViewHolder holder, int position) {
                holder.name.setText(deviceNameList.get(position));
                Log.i("MAIN", deviceNameList.get(position));

                holder.address.setText(deviceAddressList.get(position));

                holder.card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }

            @Override
            public int getItemCount() {
                return deviceNameList.size();
            }
        };
        devicesList.setHasFixedSize(false);
        devicesList.setLayoutManager(new LinearLayoutManager(this));
        devicesList.setAdapter(adapter);
    }


    class BluetoothViewHolder extends RecyclerView.ViewHolder {

        TextView name, address;
        CardView card;

        public BluetoothViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            card = itemView.findViewById(R.id.card_view);
        }
    }


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }


}
