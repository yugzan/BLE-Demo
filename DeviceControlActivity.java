/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sonostar.bletool;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.widget.AdapterView;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends ListActivity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    //private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    //private final String LIST_DATA = "DATA";
    private final String CHAR_UUID = "CHAR";
    private final String CHAR_DATA = "DATA";
    
    String[] charUUID ;
    ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
    = new ArrayList<ArrayList<HashMap<String, String>>>();
    String[] item;
    UUID serviceUUID ;
    int  servicePosition= 0;
    public static String gattCharacteristicDataValueString ;
    public static String gattServiceUUIDString ;
    public static String gattCharacteristicUUIDString ;
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
            //Log.i(TAG, "connect"+mDeviceAddress.toString())
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
       
		@Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) 
            {
            	
            	Log.i(TAG, gattServiceUUIDString);
            	Log.i(TAG, gattCharacteristicUUIDString);
            	Log.i(TAG, gattCharacteristicDataValueString);

            	displayDataControler
            	(gattServiceUUIDString,
            			gattCharacteristicUUIDString,
            			gattCharacteristicDataValueString);
        	
                
            }
        }

		private void displayDataControler(String gattServiceUUIDString,
				String gattCharacteristicUUIDString,
				String gattCharacteristicDataValueString) 
		{
			Intent intent = new Intent();
			intent.setClass(DeviceControlActivity.this, ServiceCharacteristicControler.class);
			intent.putExtra(LIST_NAME, 
					SampleGattAttributes.lookup(gattCharacteristicUUIDString, 
							getResources().getString(R.string.unknown_service)));
			intent.putExtra(LIST_UUID, gattServiceUUIDString);
			intent.putExtra(CHAR_UUID, gattCharacteristicUUIDString);
			intent.putExtra(CHAR_DATA, gattCharacteristicDataValueString);
			startActivity(intent);
			
		}
    };

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    /**
     * @ onclick 
     */
//    private final ExpandableListView.OnChildClickListener servicesListClickListner =
//            new ExpandableListView.OnChildClickListener() {
//                @Override
//                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
//                                            int childPosition, long id) {
//                    if (mGattCharacteristics != null) {
//                        final BluetoothGattCharacteristic characteristic =
//                                mGattCharacteristics.get(groupPosition).get(childPosition);
//                        final int charaProp = characteristic.getProperties();
//                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
//                            // If there is an active notification on a characteristic, clear
//                            // it first so it doesn't update the data field on the user interface.
//                            if (mNotifyCharacteristic != null) {
//                                mBluetoothLeService.setCharacteristicNotification(
//                                        mNotifyCharacteristic, false);
//                                mNotifyCharacteristic = null;
//                            }
//                            mBluetoothLeService.readCharacteristic(characteristic);
//                        }
//                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
//                            mNotifyCharacteristic = characteristic;
//                            mBluetoothLeService.setCharacteristicNotification(
//                                    characteristic, true);
//                        }
//                        return true;
//                    }
//                    return false;
//                }
//    };

    private void clearUI() {
        //mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
       
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);

        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        ListView lv = getListView();
		lv.setCacheColorHint(0);
		lv.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3)
			{
				// TODO Auto-generated method stub
				final TextView title = (TextView) view
						.findViewById(R.id.info_label);
				gattServiceUUIDString= UUID.fromString(title.getText().toString()).toString();
				serviceUUID = UUID.fromString(title.getText().toString());
				servicePosition = position;
				
				 item = new String[gattCharacteristicData.get(position).size()];
				for (int a = 0 ; a <gattCharacteristicData.get(position).size();a++)
				{
					item[a]=gattCharacteristicData.get(position).get(a).get(LIST_UUID);
						
				}
				
				//controlIntent.putExtra(LIST_UUID, serviceUUID);

				 CharaListDialog(item);
				
				
				
			}

			
		});
		
		//setListAdapter(new LeDeviceService(this));
        
    }
    public void CharaListDialog(String[] list) 
	{
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, android.R.id.text1, list)
		{
			public View getView(int position, View convertView, ViewGroup parent)
			{
				// User super class to create the View

				View v = super.getView(position, convertView, parent);
				TextView tv = (TextView) v.findViewById(android.R.id.text1);

				
				// Add margin between image and text (support various screen
				// densities)
				int dp5 = (int) (getResources().getDisplayMetrics().density + 0.5f);
				tv.setCompoundDrawablePadding(dp5);

				return v;
			}

			
		};

		new AlertDialog.Builder(this).setTitle("")
				.setNegativeButton("cancel", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int item)
					{

					}
				}).setAdapter(adapter, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int position)
					{
						//Log.i(TAG,item[position]);
					}
				}).setItems(list,
			            new DialogInterface.OnClickListener() {

			        @Override
			        public void onClick(DialogInterface dialog, int which) 
			        {
			        	if (mGattCharacteristics != null) 
	                    {
	                        final BluetoothGattCharacteristic characteristic =
	                                mGattCharacteristics.get(servicePosition).get(which);
	                        
	                        final int charaProp = characteristic.getProperties();
	                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
	                            // If there is an active notification on a characteristic, clear
	                            // it first so it doesn't update the data field on the user interface.
	                            if (mNotifyCharacteristic != null) {
	                                mBluetoothLeService.setCharacteristicNotification(
	                                        mNotifyCharacteristic, false);
	                                mNotifyCharacteristic = null;
	                            }
	                            mBluetoothLeService.readCharacteristic(characteristic);
	                        }
	                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
	                            mNotifyCharacteristic = characteristic;
	                            mBluetoothLeService.setCharacteristicNotification(
	                                    characteristic, true);
	                        }
	                        //Log.i("Characteristic UUID",characteristic.getUuid().toString());
	                        gattCharacteristicUUIDString=characteristic.getUuid().toString();
	                        //controlIntent.putExtra(CHAR_UUID, characteristic.getUuid().toString());
	                    }
			        	
			        	
//			        	Log.i(TAG,item[which]);
//			        	byte[] value = new byte[1];
//			            value[0] = (byte) (21 & 0xFF);
//			            
//			        	mBluetoothLeService.writeCharacteristic
//			        	(value, serviceUUID, UUID.fromString(item[which]));
			        	
			        	
			        }
			    }).show();

	}


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

   

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String datavalue= null;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<String> gattArrayList = new  ArrayList<String>();
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
       
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
       
        for (BluetoothGattService gattService : gattServices) {
        	//放置所有service資訊
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);
            gattArrayList.add(uuid);
            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();
           
            // Loops through available Characteristics.
           // int index =0;
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) 
            {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                
                //datavalue = SampleGattAttributes.bytesToHex(gattCharacteristics.get(index).getValue());
                
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                
                //currentCharaData.put(LIST_DATA, datavalue);
                gattCharacteristicGroupData.add(currentCharaData);
              
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
        setListAdapter(new LeDeviceServiceAdapter(this,gattArrayList));
        
    }
    
    

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
