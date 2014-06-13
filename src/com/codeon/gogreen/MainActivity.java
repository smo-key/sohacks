package com.codeon.gogreen;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.DetectedActivity;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class MainActivity extends ActionBarActivity implements ConnectionCallbacks, OnConnectionFailedListener{
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int action = intent.getIntExtra("activity", 0);
			changeText(action);
		}

	};
	private ActivityRecognitionClient mActivityRecognitionClient;
	private PendingIntent mActivityRecognitionPendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter filter = new IntentFilter();
		filter.addAction("com.codeon.gogreen.ActivityRecieved");
		registerReceiver(receiver, filter);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new HomeFragment())
                    .commit();
        }
        //Activity Recog
        mActivityRecognitionClient =
                new ActivityRecognitionClient(getApplicationContext(), (com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks) this, this);
        Intent intent = new Intent(getApplicationContext(), ActivityRecognitionIntentService.class);
        mActivityRecognitionClient.connect();
        mActivityRecognitionPendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
       
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void changeText (int action){
    	String actiontranslated = computeInterval(action);
    	Log.d("com.codeon.gogreen", actiontranslated);
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


	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onConnected(Bundle arg0) {
        if (mActivityRecognitionClient != null && mActivityRecognitionClient.isConnected()) {
            mActivityRecognitionClient.requestActivityUpdates(
                    2000,
                    mActivityRecognitionPendingIntent);
            mActivityRecognitionClient.disconnect();
        }		
	}


	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}



}
