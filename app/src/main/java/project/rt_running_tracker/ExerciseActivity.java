package project.rt_running_tracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ExerciseActivity extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback {
    private SensorManager sensorManager;
    private Sensor sensor;
    private int stepCount;
    private double travelLength;
    private double stepLength;
    private double newLength;
    private GoogleMap mMap;
    private MapView mMapView;
    private FusedLocationProviderClient fusedLocationClient;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        //Nollataan askel laskurin ja matkan laskurin arvot aina kun aktiviteetti avataan

        this.stepCount = 0;
        this.travelLength = 0.0;

        /*
            Asetetaan 0.415 kerroin, koska pituus * 0.415 = askelpituus.
            Haetaan preferenssistä käyttäjän asettama pituus, minkä
            jälkeen lasketaan askelpituus.
         */

        double multiplier = 0.415;

        SharedPreferences userPref = getSharedPreferences("SavedUserProfileData", Activity.MODE_PRIVATE);

        int height = userPref.getInt("User height", 0);

        this.stepLength = height * multiplier;

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        getLocation(googleMap);
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
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

            this.travelLength = this.stepCount * this.stepLength / 100.0;

            //Muunnetaan double kahden desimaalin tarkaksi, käyttäen Javan BigDecimalia.

            BigDecimal bd = new BigDecimal(this.travelLength).setScale(2, RoundingMode.HALF_UP);
            newLength = bd.doubleValue();

            updateUI();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        /*
            Tällä metodilla ei varsinaisesti tee mitään tähän projektiin,
            mutta pitää lisätä koska rajapinta pakottaa.
        */
    }

    private void getLocation(GoogleMap googlemap) {

        //Kysytään luvat käyttäjän paikannukseen, ellei lupia ole jo annettu

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        } else {
            Log.d("coarse_location.permission", "denied");
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        } else {
            Log.d("fine_location.permission", "denied");
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 101);
        } else {
            Log.d("background_location.permission", "denied");
        }

        //tarkastetaan onko käyttäjä antanut luvat paikannukseen
        //mikäli on haetaan käyttäjän viimeisin sijainti

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            //LocationServices avulla tallennetaan paikannustiedot muuttujaan
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Saatiin käyttäjän viimeinen tiedetty sijainti
                    if (location != null) {
                        //jos sijainti on varmasti tiedossa, asetetaan merkki käyttäjän kohdalle kartalla
                        setMarker(googlemap, location);
                    }
                }
            });
        }
    }

    //asetetaan merkki käyttäjän viimeiseen sijaintiin
    public void setMarker(GoogleMap map, Location location)    {
        Log.d("debug", String.valueOf(location.getLatitude()));
        map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Marker"));
        //map.addMarker(new MarkerOptions().position(new LatLng(7, 7)).title("Marker"));

    }

    public void onStopButtonClick(View v) {
        final GoogleMap.SnapshotReadyCallback snapshotReadyCallback = new GoogleMap.SnapshotReadyCallback() {
            Bitmap bitmap;
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSnapshotReady(@Nullable Bitmap snap) {
                bitmap = snap;
                try {
                    String path = "/data/data/project.rt_running_tracker/files/";

                    File pref = new File("/data/data/project.rt_running_tracker/shared_prefs/index.xml");

                    if (pref.exists()) {
                        SharedPreferences index = getSharedPreferences("index" ,Activity.MODE_PRIVATE);
                        int j = index.getInt("i", 0);
                        i = j;
                        i++;

                        SharedPreferences.Editor editor = index.edit();

                        editor.putInt("i", i);
                        editor.commit();
                    } else {
                        i = 0;

                        SharedPreferences index = getSharedPreferences("index", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = index.edit();

                        editor.putInt("i", i);
                        editor.commit();
                    }

                    FileOutputStream fos = new FileOutputStream(path + "image" + i + java.time.LocalDate.now() + ".png");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);

                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        GoogleMap.OnMapLoadedCallback mapLoadedCallback = new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.snapshot(snapshotReadyCallback);
            }
        };
        mMap.setOnMapLoadedCallback(mapLoadedCallback);

        SharedPreferences runPref = getSharedPreferences("juoksu" + i, MODE_PRIVATE);
        SharedPreferences.Editor editor = runPref.edit();

        Float f = (float) newLength;

        editor.putFloat("matka", f);
        editor.putInt("askeleet", this.stepCount);

        editor.commit();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void updateUI () {
        TextView tv1 = findViewById(R.id.travelLengthView);
        tv1.setText(newLength + "m");
        TextView tv = findViewById(R.id.stepView);
        tv.setText(this.stepCount + " askelta");
    }
}