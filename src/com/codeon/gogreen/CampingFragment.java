package com.codeon.gogreen;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.mashape.unirest.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class CampingFragment extends Fragment{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_camping, container,
				false);
		
		HttpResponse<String> request = Unirest.get("https://trailapi-trailapi.p.mashape.com/"
				+ "?q[activities_activity_type_name_eq]=hiking"
				+ "&lat=34.1"
				+ "&lon=-105.2"
				+ "&radius=25"
				+ "&q[state_cont]=California"
				+ "&q[country_cont]=Australia&q[city_cont]=Denver"
				+ "&q[activities_activity_name_cont]=Yellow%20River%20Trail"
				+ "&limit=25") 
				.header("X-Mashape-Authorization", "sP9Qf5DbYom8Zqvb49OSFVb2iAifJKuS") .asString();
		
		try {
			JSONObject root = (JSONObject) new JSONObject(request.toString());
			JSONArray results = root.getJSONArray("places");
			for (int i = 0; i < results.length(); i++) {
				JSONObject j = results.getJSONObject(i);
				double lat = j.getDouble("lat");
				double lng = j.getDouble("lon");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rootView;
	}
}
