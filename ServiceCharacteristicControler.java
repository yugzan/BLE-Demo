package com.sonostar.bletool;

import java.util.UUID;

import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AnalogClock;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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

	private TextView suuid_tv, data_tv, service_sen;
	private EditText service_year, service_month, service_day, service_hour,
			service_min, service_starthour, service_endhour;
	private Switch swt1;
	private SeekBar bar1;
	private BluetoothLeService mBluetoothLeService;

	private String Suuid, data, Cuuid, Cname;
	private int WRITE_ACTION = 0;
	private boolean isNotify = false;
	public static BluetoothGattCharacteristic notify_Characteristic;
	ProgressDialog progress ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.controler);
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
		
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		suuid_tv = (TextView) findViewById(R.id.service_name);
		data_tv = (TextView) findViewById(R.id.service_data);
		service_sen = (TextView) findViewById(R.id.service_sen);
		swt1 = (Switch) findViewById(R.id.service_notify);
		service_year = (EditText) findViewById(R.id.service_year);
		service_month = (EditText) findViewById(R.id.service_month);
		service_day = (EditText) findViewById(R.id.service_day);
		service_hour = (EditText) findViewById(R.id.service_hour);
		service_min = (EditText) findViewById(R.id.service_min);
		service_starthour = (EditText) findViewById(R.id.service_starthour);
		service_endhour = (EditText) findViewById(R.id.service_endhour);

		bar1 = (SeekBar) findViewById(R.id.service_seekbar);
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

				service_sen.setText("靈敏度:" + String.valueOf(bar1.getProgress()));
			}
		});
		swt1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					isNotify = true;
					mBluetoothLeService.setCharacteristicNotification(
							notify_Characteristic, true);
					progress = ProgressDialog.show(ServiceCharacteristicControler.this, "dialog title",
							  "dialog message", true);
					new Thread(new Runnable() {
						  @Override
						  public void run()
						  {
						    // do the thing that takes a long time

						    runOnUiThread(new Runnable() {
						      @Override
						      public void run()
						      {
						    	  
						        
						      }
						    });
						  }
						}).start();
					
				}
			}
		});

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			Suuid = extras.getString(LIST_UUID);
			data = extras.getString(CHAR_DATA);
			Cuuid = extras.getString(CHAR_UUID);
			Cname = extras.getString(LIST_NAME);

			if (Suuid != null && data != null && Cuuid != null) {
				// do stuff
				suuid_tv.setText("Service : \n" + Suuid);
				// write_text.setText(""+Integer.parseInt(data.trim(),16));
				data_tv.setText("\n" + Cname + "\n" + data);
				// bar1.setProgress(Integer.parseInt(data.trim(),16));
			}
			SettingUIwithUUID(SampleGattAttributes.lookup(Cuuid));
		}

	}

	private void SettingUIwithUUID(int resultID) {
		// TODO Auto-generated method stub
		String[] datasplit = data.split(" ");
		int year, month, day, hour, min;
		WRITE_ACTION = resultID;

		switch (resultID) {
		case 1:
			service_sen.setText("靈敏度"
					+ String.valueOf(Integer.valueOf(datasplit[1], 16)));
			bar1.setProgress(Integer.valueOf(datasplit[1], 16));
			year = Integer.valueOf(datasplit[3] + datasplit[2], 16);
			month = Integer.valueOf(datasplit[4], 16);
			day = Integer.valueOf(datasplit[5], 16);
			hour = Integer.valueOf(datasplit[6], 16);
			min = Integer.valueOf(datasplit[7], 16);
			service_year.setText(String.valueOf(year));
			service_month.setText(String.valueOf(month));
			service_day.setText(String.valueOf(day));
			service_hour.setText(String.valueOf(hour));
			service_min.setText(String.valueOf(min));

			break;
		case 2:
			service_sen.setText(String.valueOf(Integer
					.valueOf(datasplit[0], 16)));

			break;
		case 3:
			service_starthour.setText(String.valueOf(Integer.valueOf(
					datasplit[0], 16)));

			service_endhour.setText(String.valueOf(Integer.valueOf(
					datasplit[1], 16)));
			break;
		case 4:
			year = Integer.valueOf(datasplit[1] + datasplit[0], 16);
			month = Integer.valueOf(datasplit[2], 16);
			day = Integer.valueOf(datasplit[3], 16);
			hour = Integer.valueOf(datasplit[4], 16);
			min = Integer.valueOf(datasplit[5], 16);
			service_year.setText(String.valueOf(year));
			service_month.setText(String.valueOf(month));
			service_day.setText(String.valueOf(day));
			service_hour.setText(String.valueOf(hour));
			service_min.setText(String.valueOf(min));
			break;
		case 5:
			service_sen.setText("靈敏度"
					+ String.valueOf(Integer.valueOf(datasplit[0], 16)));
			bar1.setProgress(Integer.valueOf(datasplit[0], 16));
			break;
		case 6:

			break;
		case 7:

			break;
		case 8:

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
			Log.i(TAG, "getAction " + action);
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) 
			{

			} else if (BluetoothLeService.NOTIFY_RESULT
					.equals(action)) 
			{
				Log.i(TAG, "notify complete");
				progress.dismiss();
				CallDialog();
			} 
			else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {

			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {

			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))
			{

				String keyvalue = intent
						.getStringExtra(BluetoothLeService.EXTRA_DATA);
				Log.i(TAG, "Notify : " + keyvalue);

				// suuid_tv.setText(suuid_tv.getText().toString()+keyvalue);

			} else if (BluetoothLeService.ACTION_DATA_WRITE.equals(action)) {
				String keyvalue = intent
						.getStringExtra(BluetoothLeService.WRITE_RESULT);
				suuid_tv.setText(keyvalue);
			}

		}

		private void CallDialog() {
			// TODO Auto-generated method stub
			StringBuilder stringBuilder = null ;
			byte[] data ;
			String resultString ="";
			for (int i = 0 ;i<BluetoothLeService.NOTIFY_DATA_ARRAY.size();i++)
			{
				data = BluetoothLeService.NOTIFY_DATA_ARRAY.get(i);
				if (data != null && data.length > 0) 
				{
					stringBuilder = new StringBuilder(
							data.length);
					for (byte byteChar : data)
					{
						stringBuilder.append(String.format("%02X ", byteChar));
					}
				}
				resultString= resultString+stringBuilder.toString()+",";
			}
			
			
			
			
			
			new AlertDialog.Builder(ServiceCharacteristicControler.this)
			// items.get(i).routableAddress()
			.setTitle("Notify Complete")
			.setMessage("Length:"+BluetoothLeService.NOTIFY_DATA_ARRAY.size()+
						"\nContent: \n"+resultString)
					
			.setPositiveButton("ok",
					new DialogInterface.OnClickListener()
					{
						public void onClick(
								DialogInterface dialog,
								int which)
						
						{
							
						}
					}).show();
		}
	};

	public void onWrite(View v) {
		byte[] values = null;
		int value = 0;
		int total_1 = 0, total_2 = 0;
		switch (WRITE_ACTION) {

		case 1:

			break;

		case 2:
			values = new byte[1];
			values[0] = (byte) (1 & 0xFF);
			break;

		case 3:
			values = new byte[2];
			value = Integer.parseInt(service_starthour.getText().toString());
			values[0] = (byte) (value & 0xFF);
			value = Integer.parseInt(service_endhour.getText().toString());
			values[1] = (byte) (value & 0xFF);
			break;

		case 4:
			values = new byte[6];
			String hex = Integer.toHexString(Integer.parseInt(service_year
					.getText().toString()));
			if (hex.length() == 3) {
				total_1 = Integer.valueOf(String.valueOf(hex.charAt(1))
						+ String.valueOf(hex.charAt(2)), 16);
				total_2 = Integer.valueOf(String.valueOf(hex.charAt(0)), 16);

			}
			if (hex.length() == 4) {
				total_1 = Integer.valueOf(String.valueOf(hex.charAt(2))
						+ String.valueOf(hex.charAt(3)), 16);

				total_2 = Integer.valueOf(String.valueOf(hex.charAt(0))
						+ String.valueOf(hex.charAt(1)), 16);
			}

			values[0] = (byte) (total_1 & 0xFF);// 年
			values[1] = (byte) (total_2 & 0xFF);// 年
			value = Integer.parseInt(service_month.getText().toString());// 月
			values[2] = (byte) (value & 0xFF);
			value = Integer.parseInt(service_day.getText().toString());// 日
			values[3] = (byte) (value & 0xFF);
			value = Integer.parseInt(service_hour.getText().toString());// 時
			values[4] = (byte) (value & 0xFF);
			value = Integer.parseInt(service_min.getText().toString());// 分
			values[5] = (byte) (value & 0xFF);

			break;
		case 5:
			values = new byte[1];
			value = bar1.getProgress();
			values[0] = (byte) (value & 0xFF);
			break;

		}
		Log.i(TAG, value + "");
		Log.i(TAG, UUID.fromString(Suuid).toString());
		Log.i(TAG, UUID.fromString(Cuuid).toString());

		mBluetoothLeService.writeCharacteristic(values, UUID.fromString(Suuid),
				UUID.fromString(Cuuid));
		// tring hex = write_text.getText().toString();

		// Log.i(TAG,Integer.valueOf(hex,16).toString());

		// Log.i(TAG,write_text.getText().toString().getBytes());

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
		BluetoothLeService.NOTIFY_ISEXIST = false;
		unbindService(mServiceConnection);
		mBluetoothLeService = null;
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.NOTIFY_RESULT);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_WRITE);
		return intentFilter;
	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
	

}
