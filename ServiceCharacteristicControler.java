package com.sonostar.bletool;

import java.util.UUID;

import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AnalogClock;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.R.integer;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;

public class ServiceCharacteristicControler extends Activity {

	private final static String TAG = DeviceControlActivity.class
			.getSimpleName();
	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	private TextView mConnectionState;
	private TextView mDataField;
	private String mDeviceName;
	private String mDeviceAddress;

	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";
	private final String CHAR_UUID = "CHAR";
	private final String CHAR_DATA = "DATA";

	private TextView suuid_tv, data_tv,service_sen;
	private EditText service_year,service_month,service_day,service_hour,service_min;
	private Switch swt1;
	private SeekBar bar1;
	private BluetoothLeService mBluetoothLeService;
	
	private String Suuid , data , Cuuid , Cname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        suuid_tv 	= 	(TextView) 	findViewById(R.id.service_name);
		data_tv 	= 	(TextView) 	findViewById(R.id.service_data);
		service_sen = 	(TextView) 	findViewById(R.id.service_sen);
		swt1 = 			(Switch)	findViewById(R.id.service_notify);
		service_year = 	(EditText) 	findViewById(R.id.service_year);
		service_month = (EditText) 	findViewById(R.id.service_month);
		service_day = 	(EditText) 	findViewById(R.id.service_day);
		service_hour = 	(EditText) 	findViewById(R.id.service_hour);
		service_min = 	(EditText) 	findViewById(R.id.service_min);
		
		bar1=			(SeekBar)	findViewById(R.id.service_seekbar);
		bar1.setMax(100);
		bar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
			// seekbar 結束變更
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			// seekbar 開始變更
			}
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			// TODO seekbar 變更期間
				
				service_sen.setText("靈敏度:"+String.valueOf(bar1.getProgress()));
			}
			});

		
		Bundle extras = getIntent().getExtras();
		if (extras != null) 
		{
			 Suuid = extras.getString(LIST_UUID);
			 data = extras.getString(CHAR_DATA);
			 Cuuid = extras.getString(CHAR_UUID);
			 Cname = extras.getString(LIST_NAME);
			 
			 
			if (Suuid != null && data != null && Cuuid != null) {
				// do stuff
				suuid_tv.setText("Service : \n" + Suuid);
				//write_text.setText(""+Integer.parseInt(data.trim(),16));
				data_tv.setText("\n"+Cname+"\n" + Cuuid);
				//bar1.setProgress(Integer.parseInt(data.trim(),16));
			}
			 SettingUIwithUUID(SampleGattAttributes.lookup(Cuuid));
		}
		
		
		
		
	}

	private void SettingUIwithUUID(int resultID) 
	{
		// TODO Auto-generated method stub
		String[] datasplit= data.split(" ");
		int year,month,day,hour,min;
		
		switch (resultID)
		{
			case 1:
					service_sen.setText("靈敏度"+String.valueOf(Integer.valueOf(datasplit[1],16)));
					bar1.setProgress(Integer.valueOf(datasplit[1],16));
					year = 	Integer.valueOf(datasplit[3]+datasplit[2],16);
					month = Integer.valueOf(datasplit[4],16);
					 day = 	Integer.valueOf(datasplit[5],16);
					 hour = 	Integer.valueOf(datasplit[6],16);
					 min	= 	Integer.valueOf(datasplit[7],16);
					
					
					break;
			case 2:
					write_text.setText(String.valueOf(Integer.valueOf(datasplit[0],16)));
					bar1.setEnabled(false);
					datebtn.setEnabled(false);
					timerbtn.setEnabled(false);
					break;
			case 3:

					hour = 	Integer.valueOf(datasplit[3],16);
					min	= 	Integer.valueOf(datasplit[4],16);
					timerbtn.setCurrentHour(hour);
					timerbtn.setCurrentMinute(min);
				
					break;
			case 4:
					year = 	Integer.valueOf(datasplit[2]+datasplit[1],16);
					month = 	Integer.valueOf(datasplit[3],16);
					day = 		Integer.valueOf(datasplit[4],16);
					hour = 	Integer.valueOf(datasplit[5],16);
					min	= 	Integer.valueOf(datasplit[6],16);
					datebtn.updateDate(year, month, day);
					timerbtn.setCurrentHour(hour);
					timerbtn.setCurrentMinute(min);
					break;
			case 5:
					write_text.setText
					(String.valueOf(Integer.valueOf(datasplit[0],16)));
					bar1.setEnabled(true);
					bar1.setProgress(Integer.valueOf(datasplit[0],16));
					datebtn.setEnabled(false);
					timerbtn.setEnabled(false);
					break;
			case 6:
				datebtn.setEnabled(false);
				timerbtn.setEnabled(false);
				bar1.setEnabled(false);
					break;
			case 7:
				datebtn.setEnabled(false);
				timerbtn.setEnabled(false);
				bar1.setEnabled(false);
					break;
			case 8:
				datebtn.setEnabled(false);
				timerbtn.setEnabled(false);
				bar1.setEnabled(false);
					break;
		}
		
		
		
		
		
	}

	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			mBluetoothLeService.connect(mDeviceAddress);
			// Log.i(TAG, "connect"+mDeviceAddress.toString())
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {

			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {

			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

			}
			else if (BluetoothLeService.ACTION_DATA_WRITE.equals(action)) {
				String keyvalue = intent.getStringExtra(BluetoothLeService.WRITE_RESULT);
				
//				//Integer.valueOf("FFFF",16).toString();
//				bar1.setProgress(Integer.parseInt(keyvalue.trim(),16));
//				write_text.setText(bar1.getProgress()+"");
			}
		}
	};
	
	
	public void onWrite(View v)
	{
		//tring hex = write_text.getText().toString();
    	
    	//Log.i(TAG,Integer.valueOf(hex,16).toString());
    	
    	//Log.i(TAG,write_text.getText().toString().getBytes());
		byte[] values = new byte[1];
//        value[0] = (byte) (21 & 0xFF);
    	int value = bar1.getProgress();
    	
    	values[0] = (byte) (value & 0xFF);
    	Log.i(TAG,write_text.getText().toString());
    	Log.i(TAG,UUID.fromString(Suuid).toString());
    	Log.i(TAG,UUID.fromString(Cuuid).toString());
    	
    	mBluetoothLeService.writeCharacteristic
    	(values, UUID.fromString(Suuid), 
    			UUID.fromString(Cuuid));
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

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_WRITE);
		return intentFilter;
	}
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}

}
