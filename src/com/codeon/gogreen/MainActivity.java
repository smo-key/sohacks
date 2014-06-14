package com.codeon.gogreen;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.DetectedActivity;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
	private String googlekey = "AIzaSyBV15lTOpTwK2Jkv_zxWwfRyU8DsasucAY";
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] drawerNames = {"Parks"};
    private ActionBarDrawerToggle mDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter filter = new IntentFilter();
		filter.addAction("com.codeon.gogreen.ActivityRecieved");
		registerReceiver(receiver, filter);
     
        //Activity Recog
        mActivityRecognitionClient =
                new ActivityRecognitionClient(getApplicationContext(), (com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks) this, this);
        Intent intent = new Intent(getApplicationContext(), ActivityRecognitionIntentService.class);
        mActivityRecognitionClient.connect();
        mActivityRecognitionPendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, drawerNames));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
                ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (savedInstanceState == null) {
            selectItem(0);
        }
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
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

   	/** Swaps fragments in the main content view */
   	private void selectItem(int position) {
   	    // Create a new fragment and specify the planet to show based on position
   		Fragment fragment;
   	    switch (position){
   	    	case 0:
   	    		fragment = new HomeFragment();
   	    	default:
   	    		fragment = new HomeFragment();
   	    }
   	    // Insert the fragment by replacing any existing fragment
   	    FragmentManager fragmentManager = getSupportFragmentManager();
   	    fragmentManager.beginTransaction()
   	                   .replace(R.id.container, fragment)
   	                   .commit();

   	    // Highlight the selected item, update the title, and close the drawer
   	    mDrawerList.setItemChecked(position, true);
   	    setTitle(drawerNames[position]);
   	    mDrawerLayout.closeDrawer(mDrawerList);
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
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	        selectItem(position);
	    }

	}
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

}
