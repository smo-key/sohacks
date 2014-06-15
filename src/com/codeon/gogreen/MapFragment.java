package com.codeon.gogreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {

	private SupportMapFragment fragment;
	private GoogleMap map;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_map, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		FragmentManager fm = getChildFragmentManager();
		fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
		if (fragment == null) {
			fragment = SupportMapFragment.newInstance();
			fm.beginTransaction().replace(R.id.map, fragment).commit();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (map == null) {
			map = fragment.getMap();
			SharedPreferences s = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			JSONObject root;
			try {
				root = (JSONObject) new JSONObject(s.getString("json", ""));
				JSONArray results = root.getJSONArray("results");
				for (int i = 0; i < results.length(); i++) {
					// Single loc
					JSONObject j = results.getJSONObject(i);
					JSONObject geometry = j.getJSONObject("geometry");
					JSONObject location = geometry.getJSONObject("location");
					double lat = location.getDouble("lat");
					double lng = location.getDouble("lng");
					String name = j.getString("name");
					String address = j.getString("vicinity");
					LatLng locallatlng = new LatLng(lat, lng);
					map.addMarker(new MarkerOptions().position(locallatlng)
							.title(name).snippet(address));
					map.moveCamera(CameraUpdateFactory.newLatLngZoom(
							locallatlng, 12));

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		map.setMyLocationEnabled(true);

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		try {
			SupportMapFragment fragment = (SupportMapFragment) getActivity()
					.getSupportFragmentManager().findFragmentById(R.id.map);
			if (fragment != null)
				getFragmentManager().beginTransaction().remove(fragment)
						.commit();

		} catch (IllegalStateException e) {
			// handle this situation because you are necessary will get
			// an exception here :-(
		}
	}

}