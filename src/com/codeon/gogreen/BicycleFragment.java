package com.codeon.gogreen;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BicycleFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_bicycle, container,
				false);
		return rootView;
	}
	
}
