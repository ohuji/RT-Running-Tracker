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

public class ExerciseActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private int stepCount = 0;
    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

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
            this.isRunning = true;
        } else {
            Log.d("sensor.present", "sensor is not present");
            this.isRunning = false;
        }
    }

    protected void onResume() {
        super.onResume();

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.registerListener(this, this.sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    protected void onPause() {
        super.onPause();

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.unregisterListener(this, this.sensor);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == this.sensor) {
            this.stepCount = (int) event.values[0];
            TextView tv = findViewById(R.id.stepView);
            tv.setText(String.valueOf(this.stepCount));
            Log.d("sensor.step", Integer.toString(this.stepCount));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void updateUI() {

    }
}