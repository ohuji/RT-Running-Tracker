package project.rt_running_tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExerciseActivity extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback {

    //Sensori muuttujat
    private SensorManager sensorManager;
    private Sensor sensor;
    private int stepCount;
    private double travelLength;
    private double stepLength;
    private double newLength;

    //Google Maps muuttujat
    private GoogleMap mMap;
    private MapView mMapView;
    private FusedLocationProviderClient fusedLocationClient;
    Handler handler = new Handler();
    Runnable runnable;
    protected boolean keepGoing = true;
    private double lat = 0.0;
    private double lng = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        //haetaan ja kysytään käyttäjän lupia
        permissions();

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

        /*
        Tarkastetaan onko käyttäjä antanut luvat laitteen paikannukseen
        Jos luvat on annettu, käytetään setMyLocationEnabled metodia sallimaan "location layer",
        mikä sallii laitteen hyödyntämään nykyistä sijantia
         */

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            googleMap.setMyLocationEnabled(true);
        }

        //toistetaan drawPolylines metodi X sekunnin välein
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, 3000);

                drawPolylines(googleMap);

            }
        }, 3000);
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

    private void permissions()  {

        /*
            Jos luvat sensorien käyttöön tai käyttäjän paikantamiseen ei olla annettu,
            kysytään lupaa niiden käyttöön.
         */

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 101);
        } else {
            Log.d("sensor.permission", "denied");
        }
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

        //Haetaan alkukoordinaatit lat ja lng muuttujille

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
        //LocationServices avulla tallennetaan paikannustiedot muuttujaan
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                }
            }
        });
        }
    }

    private void drawPolylines(GoogleMap googlemap) {

        //tarkastetaan onko käyttäjä antanut luvat paikannukseen
        //mikäli on haetaan käyttäjän viimeisin sijainti

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            //LocationServices avulla tallennetaan paikannustiedot muuttujaan
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    //haettiin käyttäjän viimeisin sijainti ja nyt tarkistetaan vielä löytyihän se if lauseella

                    if (location != null) {

                        //piirretään polyline aiemmin tallennettujen lat ja lng muuttujien arvojen ja nykyisten koordinaattien välille

                        Polyline polyline1 = googlemap.addPolyline(new PolylineOptions()
                                .add(new LatLng(lat, lng),
                                        new LatLng(location.getLatitude(), location.getLongitude())));

                        //tehdään joku tagi
                        polyline1.setTag("A");

                        //tallennetaan viimeisimmät koordinaatit muuttujiin
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                    }
                }
            });
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //LocationServices avulla tallennetaan paikannustiedot muuttujaan
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    //haettiin käyttäjän viimeisin sijainti ja nyt tarkistetaan vielä löytyihän se if lauseella

                    if (location != null) {

                        //piirretään polyline aiemmin tallennettujen lat ja lng muuttujien arvojen ja nykyisten koordinaattien välille

                        Polyline polyline1 = googlemap.addPolyline(new PolylineOptions()
                                .add(new LatLng(lat, lng),
                                        new LatLng(location.getLatitude(), location.getLongitude())));

                        //tehdään joku tagi
                        polyline1.setTag("A");

                        //tallennetaan viimeisimmät koordinaatit muuttujiin
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                    }
                }
            });
        }
    }

    private void updateUI () {

        TextView tv1 = findViewById(R.id.travelLengthView);
        tv1.setText(newLength + "m");
        TextView tv = findViewById(R.id.stepView);
        tv.setText(String.valueOf(this.stepCount));

    }
}