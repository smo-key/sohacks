package com.codeon.gogreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class StepsFragment extends Fragment implements SensorEventListener {
	private SensorManager sensorManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_steps, container,
				false);
		Button button = (Button) rootView.findViewById(R.id.resetbutton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				SharedPreferences s = PreferenceManager
						.getDefaultSharedPreferences(getActivity());
				Editor e = s.edit();
				e.putInt("stepsinit", -1);
				e.commit();
				TextView count = (TextView) getView().findViewById(R.id.count);
				count.setText("0");
			}
		});
		SharedPreferences s = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		TextView count = (TextView) rootView.findViewById(R.id.count);
		TextView life = (TextView) rootView.findViewById(R.id.timewalked); 
		count.setText(s.getInt("stepsinit", 0) + "");
		life.setText("Total steps recorded: "+ s.getInt("stepslife", 0));

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		sensorManager = (SensorManager) getActivity().getSystemService(
				Context.SENSOR_SERVICE);
		Sensor countSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
		if (countSensor != null) {
			sensorManager.registerListener(this, countSensor,
					SensorManager.SENSOR_DELAY_UI);
		} else {
			Toast.makeText(getActivity(), "Count sensor not available!",
					Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		// if you unregister the last listener, the hardware will stop detecting
		// step events
		sensorManager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		SharedPreferences s = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		TextView count = (TextView) getView().findViewById(R.id.count);
		TextView life = (TextView) getView().findViewById(R.id.timewalked);
		Editor e = s.edit();
		Log.d("com.codeone.gogreen", event.values[0] + "");
		if (s.getInt("stepsinit", -1) == -1) {
			e.putInt("stepsinit", (int) event.values[0]);
			if (count != null)
				count.setText(0 + "");
		} else {
			int set = s.getInt("stepslife", 0) + 1;
			e.putInt("stepslife", set);
			if (count != null && life != null) {
				count.setText(Math.round(event.values[0]
						- s.getInt("stepsinit", 0))
						+ "");
				life.setText("Total steps recorded: " + set);
			}
		}
		e.commit();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

}
