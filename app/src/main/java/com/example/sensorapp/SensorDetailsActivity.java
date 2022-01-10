package com.example.sensorapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SensorDetailsActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensorLight;
    private TextView sensorLightTextView;
    private TextView sensorDetails;
    private String SENSOR_TAG = "SensorDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);

        sensorLightTextView = findViewById(R.id.sensor_name);
        sensorDetails = findViewById(R.id.sensor_details);

        String name = getIntent().getExtras().getString(SensorActivity.KEY_EXTRA_SENSOR_NAME);
        sensorLightTextView.setText(name);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if(name.equals("Goldfish Light sensor") ) {
            sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        } else if(name.equals("Goldfish Pressure sensor")) {
            sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        }

        if(sensorLight == null) {
            sensorLightTextView.setText(R.string.missing_sensor);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        LinearLayout currentLayout = (LinearLayout) findViewById(R.id.sensor_view);

        float currentValue = event.values[0];
        float max = sensorLight.getMaximumRange();

        switch (sensorType) {
            case Sensor.TYPE_LIGHT:
            case Sensor.TYPE_PRESSURE:
                sensorDetails.setText(getResources().getString(R.string.sensor_details, currentValue));
                if(currentValue > max / 2 + max / 4) {
                    currentLayout.setBackgroundResource(R.color.color);
                } else if(currentValue < max / 2 - max / 4) {
                    currentLayout.setBackgroundResource(R.color.lighterPink);
                } else currentLayout.setBackgroundResource(R.color.lightPink);
                break;
            default:
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(SENSOR_TAG, "Widzisz mnie???");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(sensorLight != null) {
            sensorManager.registerListener(this, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        sensorManager.unregisterListener(this);
    }
}
