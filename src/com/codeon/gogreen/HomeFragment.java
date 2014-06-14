package com.codeon.gogreen;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends Fragment implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
LocationListener{
	/**
	 * A placeholder fragment containing a simple view.
	 */
	private String googlekey = "AIzaSyBV15lTOpTwK2Jkv_zxWwfRyU8DsasucAY";
	private LocationClient mLocationClient;
    private Location mCurrentLocation;
	private LocationRequest mLocationRequest;
	// Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
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
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationClient.connect();
		return rootView;
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
    public class PlacesTask extends AsyncTask<String, String, String> {
        ProgressDialog dialog;

        public PlacesTask() {

        }

        @Override
        protected String doInBackground(String[] paramArrayOfString) {
        	try {
        		String loc = mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude();
    			URL url = new URL("https://maps.googleapis.com/maps/api/place/search/json"
    					+ "?location=" + loc
    					+ "&radius=5000"
    					+ "&sensor=false"
    					+ "&types=park"
    					+ "&key=" + googlekey);
    		    InputStream in = url.openStream();
    		    InputStreamReader reader = new InputStreamReader(in);
    		    Log.d("com.codeon.gogreen", convertStreamToString(in));
    		} catch (MalformedURLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
           
            return "";
        }

        @Override
        protected void onPostExecute(String str) {
            this.dialog.dismiss();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (dialog == null){
                this.dialog = new ProgressDialog(getActivity());
                this.dialog.setMessage("Creating...");
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
		 Log.d("com.codeon.gogreen", "Lat" + newloc.getLatitude() + "Long" + newloc.getLongitude());
	 }
}
