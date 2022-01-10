package com.example.sensorapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SensorActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private List<Sensor> sensorList;
    private RecyclerView recyclerView;
    private SensorAdapter adapter;
    private boolean isCountVisible;
    private static final String KEY_IS_VISIBLE = "isCountVisible";
    public static final String KEY_EXTRA_SENSOR_NAME = "com.example.sensor.NAME";

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        recyclerView = findViewById(R.id.sensor_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(adapter == null) {
            adapter = new SensorAdapter(sensorList);
            recyclerView.setAdapter(adapter);
        }
        else {
            adapter.notifyDataSetChanged();
        }

        if(savedInstanceState != null) {
            isCountVisible = savedInstanceState.getBoolean(KEY_IS_VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_VISIBLE, isCountVisible);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sensor_menu, menu);
        MenuItem count = menu.findItem(R.id.show_sensor_count);

        if(isCountVisible) {
            count.setTitle(R.string.hide_sensor_menu);
        } else {
            count.setTitle(R.string.show_sensor_menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        isCountVisible = !isCountVisible;
        updateCount();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCount();
    }

    private void updateCount() {
        int sensors = 0;
        for(Sensor sensor : sensorList) {
            sensors++;
        }
        String count = getString(R.string.sensor_count, sensors);
        if(!isCountVisible) {
            count = null;
        }
        getSupportActionBar().setSubtitle(count);
    }

    private class SensorAdapter extends RecyclerView.Adapter<SensorHolder>{

        private List<Sensor> sensorList;

        public SensorAdapter(List<Sensor> sensorList) {
            this.sensorList = sensorList;
        }

        @NonNull
        @Override
        public SensorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = getLayoutInflater();
            return new SensorHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull SensorHolder holder, int position) {
            Sensor sensor = sensorList.get(position);
            holder.bind(sensor);
        }

        @Override
        public int getItemCount() {
            return sensorList.size();
        }
    }

    private class SensorHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView nameView;
        ImageView iconView;
        Sensor sensor;
        String SENSOR_TAG = "SensorActivity";

        public SensorHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.sensor_list_item, parent, false));
            itemView.setOnClickListener(this);

            nameView = itemView.findViewById(R.id.sensor_item_name);
            iconView = (AppCompatImageView) itemView.findViewById(R.id.action_image);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            //if(sensor.getName() == "Goldfish Light sensor") {
                Intent intent = new Intent(SensorActivity.this, SensorDetailsActivity.class);
                intent.putExtra(KEY_EXTRA_SENSOR_NAME, sensor.getName());
                startActivity(intent);
            //}
        }

        public void bind(Sensor sensor) {
            this.sensor = sensor;
            nameView.setText(sensor.getName());
            if(sensor.getName() == "Goldfish Pressure sensor" || sensor.getName() == "Goldfish Light sensor") {
                nameView.setTypeface(null, Typeface.BOLD);
            }
            iconView.setImageResource(R.drawable.ic_action_name);
            Log.d(SENSOR_TAG, "Producer: " + sensor.getVendor() + " Power: " + sensor.getMaximumRange());

        }
    }
}