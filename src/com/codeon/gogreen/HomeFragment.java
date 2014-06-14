package com.codeon.gogreen;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fima.cardsui.objects.Card;
import com.fima.cardsui.views.CardUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class HomeFragment extends Fragment implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
	/**
	 * A placeholder fragment containing a simple view.
	 */
	private CardUI cardView;
	private String googlekey = "AIzaSyBV15lTOpTwK2Jkv_zxWwfRyU8DsasucAY";
	private LocationClient mLocationClient;
	private Location mCurrentLocation;
	private LocationRequest mLocationRequest;
	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
	// Update frequency in milliseconds
	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
			* UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	// A fast frequency ceiling in milliseconds
	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND
			* FASTEST_INTERVAL_IN_SECONDS;

	public HomeFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		mLocationClient = new LocationClient(getActivity(), this, this);

		// Create the LocationRequest object
		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set the update interval to 5 seconds
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		// Set the fastest update interval to 1 second
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		mLocationClient.connect();
		setHasOptionsMenu(true);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		cardView = (CardUI) getActivity().findViewById(R.id.cardsview);
	}

	public void onResume() {
		super.onResume();
		if (mCurrentLocation != null) {
			new PlacesTask().execute();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		// Connect the client.
		mLocationClient.connect();
	}

	@Override
	public void onStop() {
		// Disconnecting the client invalidates it.
		mLocationClient.disconnect();
		super.onStop();
	}

	public void genCards(final ArrayList<Park> parkslist) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				cardView.clearCards();
				for (Park p : parkslist) {
					cardView.addCard(new MyPlayCard(p.getName(),
							p.getAddress(), "#669900", "#bebebe", false, false,
							p.getDist()));
				}
			}
		});
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.parks_menu, menu);
	}

	public class PlacesTask extends AsyncTask<String, String, String> {
		ProgressDialog dialog;

		public PlacesTask() {

		}

		@Override
		protected String doInBackground(String[] paramArrayOfString) {
			ArrayList<Park> parkslist = new ArrayList<Park>();
			try {
				String loc = mCurrentLocation.getLatitude() + ","
						+ mCurrentLocation.getLongitude();
				URL url = new URL(
						"https://maps.googleapis.com/maps/api/place/search/json"
								+ "?location=" + loc + "&radius=3000"
								+ "&sensor=false" + "&types=park" + "&key="
								+ googlekey);

				InputStream in = url.openStream();
				InputStreamReader reader = new InputStreamReader(in);
				String parsedjson = convertStreamToString(in);
				JSONObject root;
				SharedPreferences s = PreferenceManager
						.getDefaultSharedPreferences(getActivity());
				Editor ed = s.edit();
				ed.putString("json", parsedjson);
				ed.commit();
				try {
					root = (JSONObject) new JSONObject(parsedjson.substring(parsedjson.indexOf("{"), parsedjson.lastIndexOf("}") + 1));
					JSONArray results = root.getJSONArray("results");
					for (int i = 0; i < results.length(); i++) {
						// Single loc
						JSONObject j = results.getJSONObject(i);
						JSONObject geometry = j.getJSONObject("geometry");
						JSONObject location = geometry
								.getJSONObject("location");
						double lat = location.getDouble("lat");
						double lng = location.getDouble("lng");
						double distance = distance(
								mCurrentLocation.getLatitude(),
								mCurrentLocation.getLongitude(), lat, lng);
						if (distance < .250){
							Toast.makeText(getActivity(), "Checked in to a Park!", Toast.LENGTH_LONG).show();
							Editor blah = s.edit();
							blah.putInt("parks", s.getInt("parks", 0)+10);
							blah.commit();
						}
						else {
							
						}
						Log.d("com.codeon.gogreen", distance + "");
						parkslist.add(new Park(j.getString("vicinity"), j
								.getString("name"), distance));

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Log.d("com.codeon.gogreen", parsedjson);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			genCards(parkslist);
			return "";
		}

		@Override
		protected void onPostExecute(String str) {
			this.dialog.dismiss();

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null) {
				this.dialog = new ProgressDialog(getActivity());
				this.dialog.setMessage("Updating...");
				this.dialog.show();
			}
		}
	}


	static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		mCurrentLocation = mLocationClient.getLastLocation();
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		new PlacesTask().execute();
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location newloc) {
		// Report to the UI that the location was updated
		mCurrentLocation = newloc;
		Log.d("com.codeon.gogreen", "Lat" + newloc.getLatitude() + "Long"
				+ newloc.getLongitude());
	}

	public static double distance(double lat1, double lon1, double lat2,
			double lon2) {

		final int R = 6371; // Radius of the earth
		Double dLat = Math.toRadians(lat2 - lat1);
		Double dLon = Math.toRadians(lon2 - lon1);
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);
		Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
				* Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		Double distance = R * c;
		return distance;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {
		case R.id.checkin:
			new PlacesTask().execute();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
