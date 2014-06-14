package com.codeon.gogreen;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.codeon.gogreen.HomeFragment.PlacesTask;
import com.fima.cardsui.views.CardUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.UnirestException;

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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class CampingFragment extends Fragment implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
	private LocationClient mLocationClient;
	private Location mCurrentLocation;
	private LocationRequest mLocationRequest;
	private CardUI cardView;
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_camping, container,
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
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mCurrentLocation != null) {
			new PlacesTask().execute();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		cardView = (CardUI) getActivity().findViewById(R.id.campcard);
	}

	public class PlacesTask extends AsyncTask<String, String, String> {
		ProgressDialog dialog;

		public PlacesTask() {

		}

		@Override
		protected String doInBackground(String[] paramArrayOfString) {
			ArrayList<Park> parkslist = new ArrayList<Park>();
			ClassLoader classLoader = CampingFragment.class.getClassLoader();
			URL resource = classLoader.getResource("org/apache/http/message/BasicLineFormatter.class");
			System.out.println(resource);
			String request;
			request = httpGet("https://trailapi-trailapi.p.mashape.com/"
					+ "?q[activities_activity_type_name_eq]=hiking&lat=34.1"
					+ "&lat=" + mCurrentLocation.getLatitude()
					+ "&lon=" + mCurrentLocation.getLongitude()
					+ "&radius=50"
					+ "&limit=15");
			Log.d("com.codeon.gogreen", request);
			try {
				JSONObject root = (JSONObject) new JSONObject(request.substring(request.indexOf("{"), request.lastIndexOf("}") + 1));;
				JSONArray results = root.getJSONArray("places");
				for (int i = 0; i < results.length(); i++) {
					// Single loc
					JSONObject j = results.getJSONObject(i);
					double lat = j.getDouble("lat");
					double lng = j.getDouble("lon");
					double distance = distance(
							mCurrentLocation.getLatitude(),
							mCurrentLocation.getLongitude(), lat, lng);

					parkslist.add(new Park(j.getString("city") + ", " + j.getString("state"), j
							.getString("name"), distance));
					
				}
			} catch (JSONException e) {
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
	   public String httpGet(String s) {
		    String url = s;
		    StringBuilder body = new StringBuilder();
		    HttpClient httpclient = new DefaultHttpClient(); // create new httpClient
		    HttpGet httpGet = new HttpGet(url); // create new httpGet object
		    httpGet.setHeader("X-Mashape-Authorization","sP9Qf5DbYom8Zqvb49OSFVb2iAifJKuS");



		    try {
		        org.apache.http.HttpResponse response = httpclient.execute(httpGet); // execute httpGet
		        StatusLine statusLine = response.getStatusLine();
		        int statusCode = statusLine.getStatusCode();
		        if (statusCode == HttpStatus.SC_OK) {
		            // System.out.println(statusLine);
		            body.append(statusLine + "\n");
		            HttpEntity e = response.getEntity();
		            String entity = EntityUtils.toString(e);
		            body.append(entity);
		        } else {
		            body.append(statusLine + "\n");
		            // System.out.println(statusLine);
		        }
		    } catch (ClientProtocolException e) {
		        e.printStackTrace();
		    } catch (IOException e) {
		        e.printStackTrace();
		    } 
		    return body.toString(); // return the String
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
}
