package project.rt_running_tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

public class ExerciseActivity extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback {
    private SensorManager sensorManager;
    private Sensor sensor;
    private int stepCount;
    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        //Nollataan laskurin arvo aina kun aktiviteetti avataan

        this.stepCount = 0;

        /*
            Jos luvat sensorien käyttöön ei olla annettu,
            kysytään lupaa sensorien käyttöön.
         */

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        } else {
            Log.d("sensor.permission", "denied");
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (countSensor != null) {
            this.sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        } else {
            Log.d("sensor.present", "sensor is not present");
        }

        //mMapView variable is made from MapView map
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.registerListener(this, this.sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        mMapView.onResume();
    }

    //map's position and marker is set
    @Override
    public void onMapReady(GoogleMap map)   {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.unregisterListener(this, this.sensor);
        }
    }

    @Override
    protected void onDestroy()  {
        super.onDestroy();
    }

    @Override
    public void onLowMemory()   {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == this.sensor) {
            this.stepCount++;

            TextView tv = findViewById(R.id.stepView);
            tv.setText(String.valueOf(this.stepCount));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        /*
            Tällä metodilla ei varsinaisesti tee mitään tähän projektiin,
            mutta pitää lisätä koska rajapinta pakottaa.
        */
    }

    public void updateUI() {
    }
}