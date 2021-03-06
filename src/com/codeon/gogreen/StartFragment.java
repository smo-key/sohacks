package com.codeon.gogreen;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StartFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home2, container,
				false);
		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
		SharedPreferences s = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		int bikingtotalint = s.getInt("bikingtotal", 0);
		int parkstotalint = s.getInt("parks", 0);
		int walking = s.getInt("stepslife", 0);
		int totalpoints = bikingtotalint + parkstotalint + (walking / 100);
		TextView bikingtotal = (TextView) getView().findViewById(R.id.biketime);
		TextView parkstotal = (TextView) getView().findViewById(R.id.parkpoint);
		TextView total = (TextView) getView().findViewById(R.id.totalpoint);
		TextView steps = (TextView) getView().findViewById(R.id.stepstaken);
		bikingtotal.setText("" + bikingtotalint);
		parkstotal.setText("" + parkstotalint);
		total.setText("" + totalpoints);
		steps.setText("" + walking);
	}

}