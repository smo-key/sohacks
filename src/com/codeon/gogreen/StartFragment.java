package com.codeon.gogreen;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v4.app.Fragment;

public class StartFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home2, container,
				false);
		return rootView;
	}
	@Override
	public void onStart(){
		super.onStart();
		SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(getActivity());
		int bikingtotalint = s.getInt("bikingtotal", 0);
		int parkstotalint = s.getInt("parks", 0);
		int totalpoints = bikingtotalint + parkstotalint;
		TextView bikingtotal = (TextView) getView().findViewById(R.id.biketime);
		TextView parkstotal = (TextView) getView().findViewById(R.id.parkpoint);
		TextView total = (TextView) getView().findViewById(R.id.totalpoint);
		bikingtotal.setText("Your biking points are: " + bikingtotal);
		parkstotal.setText("Your parks points are: " + parkstotal);
		total.setText("Your total points are: " + totalpoints);
	}
	
}