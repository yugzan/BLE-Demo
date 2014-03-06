package com.sonostar.bletool;

import java.util.ArrayList;
import java.util.UUID;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LeDeviceServiceAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final ArrayList<String> values;
	//private final ArrayList<String> locvalues;
	//private final ArrayList<String> classvalues;
	//private final ArrayList<String> gpsvalues;
	//private final ArrayList<String> idvalues;

	public LeDeviceServiceAdapter(Context context, ArrayList<String> resultlist) {
		
		
		super(context, R.layout.main_list,resultlist);

		this.context = context;
		this.values = resultlist;
//		this.locvalues = locationlist;
//		this.classvalues = classlist;
//		this.gpsvalues =gpslist;
//		this.idvalues =idlist;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.info_inflater, parent, false);
		
		TextView textView = (TextView) rowView.findViewById(R.id.info_label);
		//TextView lat = (TextView) rowView.findViewById(R.id.info_content);
		UUID a = UUID.fromString(values.get(position));
		textView.setText(a.toString());
		return rowView;
	}
}
