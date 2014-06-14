package com.codeon.gogreen;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BicycleFragment extends Fragment {
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int action = intent.getIntExtra("activity", 0);
			int timebiking = intent.getIntExtra("biking",0); //problem is here!
			changeText(action,timebiking);
		}

	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_bicycle, container,
				false);
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.codeon.gogreen.ActivityRecieved");
		getActivity().registerReceiver(receiver, filter);
		
		
		return rootView;
	}    
	@Override
    public void onStart() {
        super.onStart();
    	TextView timebiked = (TextView) getView().findViewById(R.id.timebiked);
    	if (timebiked != null) {
        	SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(getActivity());
        	timebiked.setText("Total Time Biked: " + s.getInt("bikingtotal", 0) + " Minutes" );
    	}
    }
    private void changeText (int action, int timebiking){
    	if (getView()!= null){
        	TextView time = (TextView)getView().findViewById(R.id.time);
        	TextView timesec = (TextView) getView().findViewById(R.id.timedesc);
        	TextView timebiked = (TextView) getView().findViewById(R.id.timebiked);
        	SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(getActivity());
        	timebiking = s.getInt("bikingtemp", 0);
        	Log.d("com.codeon.gogreen", timebiking + "");
        	timebiked.setText("Total Time Biked: " + s.getInt("bikingtotal", 0) + " Minutes" );
        	if (timebiking == 0){
          		time.setText("Start Biking!");
        		timesec.setText("This page will display your trip time");
        	}
        	else{
        		time.setText(timebiking + " Minutes");
        		timesec.setText("Biking this Session");
        	}
    	}
    	Log.d("com.codeon.gogreen", "Action: " + action);
    }
   	private String computeInterval(int action) {
		switch(action){
			case DetectedActivity.IN_VEHICLE:
				return "Car";
			case DetectedActivity.ON_BICYCLE:
				return "Bicycle";
			case DetectedActivity.STILL:
				return "Still";
			case DetectedActivity.UNKNOWN:
				return "Unkown";

		}
		return "";
	}
}
