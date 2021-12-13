package project.rt_running_tracker;

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
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

import java.time.LocalDate;

public class ExerciseActivity extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback {

    //Sensori instanssimuuttujat
    private SensorManager sensorManager;
    private Sensor sensor;
    private int stepCount;
    private double travelLength;
    private double stepLength;
    private double newLength;

    //Google Maps instanssimuuttujat
    private GoogleMap mMap;
    private MapView mMapView;
    private FusedLocationProviderClient fusedLocationClient;
    Handler handler = new Handler();
    Runnable runnable;
    private double lat = 0.0;
    private double lng = 0.0;
    Context context = this;
  
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        sensorPermissions();

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

        //Pyydetään luvat käyttäjän paikantamiseen ennen kartan luomista
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            //mMapView muuttuja tehdään MapView kartasta
            mMapView = (MapView) findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);

            mMapView.getMapAsync(this);
        }
    }

    //Kysytään sensorien käyttöön luvat
    public void sensorPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.registerListener(this, this.sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            mMapView.onResume();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        /*
            Tarkastetaan onko käyttäjä antanut luvat laitteen paikannukseen.
            Jos luvat on annettu, käytetään setMyLocationEnabled metodia sallimaan "location layer",
            mikä sallii laitteen hyödyntämään nykyistä sijantia.
            Kartan POI pisteet otetaan pois käyttämällä json tiedostoa.
            Haetaan ja asetetaan alkukoordinaatit lat ja lng muuttujille.
            Asetetaan kartta käyttäjän kohdalle lat ja lng muuttujilla ja zoomataan lähelle käyttäjää.
        */

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            googleMap.setMyLocationEnabled(true);

            //Haetaan map_style json tiedosto ja poistetaan POI pisteet
            googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));

            //LocationServices avulla tallennetaan paikannustiedot muuttujaan
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {

                        //Asetetaan lat ja lng muuttujat
                        lat = location.getLatitude();
                        lng = location.getLongitude();

                        //Liikutetaan kamera käyttäjän kohdalle ja zoomataan lähemmäs
                        LatLng currentLocation = new LatLng(lat, lng);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15.0f));
                    }
                }
            });
        }

        //Toistetaan drawPolylines metodi X sekunnin välein
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, 3000);

                drawPolylines(googleMap);

            }
        }, 3000);
        this.mMap = googleMap;
    }

    @Override
    protected void onPause() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            mMapView.onPause();
        }

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

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            mMapView.onLowMemory();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            mMapView.onSaveInstanceState(outState);
        }
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

    private void drawPolylines(GoogleMap googlemap) {

        /*
        Tarkastetaan onko käyttäjä antanut luvat paikannukseen
        Mikäli on haetaan käyttäjän viimeisin sijainti
        */

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            //LocationServices avulla tallennetaan paikannustiedot muuttujaan
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    //Haettiin käyttäjän viimeisin sijainti ja nyt tarkistetaan vielä löytyihän se if lauseella

                    if (location != null) {

                        //Piirretään polyline aiemmin tallennettujen lat ja lng muuttujien arvojen ja nykyisten koordinaattien välille

                        Polyline polyline1 = googlemap.addPolyline(new PolylineOptions()
                                .add(new LatLng(lat, lng),
                                        new LatLng(location.getLatitude(), location.getLongitude())));

                        //Asettaa polylinen värin, leveyden ja päätyjen muodon
                        polyline1.setColor(ContextCompat.getColor(context, R.color.teal_200));
                        polyline1.setWidth(20);
                        polyline1.setEndCap(new RoundCap());
                        polyline1.setStartCap(new RoundCap());

                        //Tehdään joku tagi
                        polyline1.setTag("A");

                        //Tallennetaan viimeisimmät koordinaatit muuttujiin
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                    }
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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

        SharedPreferences index = getSharedPreferences("index", Activity.MODE_PRIVATE);
        int j = index.getInt("i", 0);
        i = j;

        SharedPreferences runPref = getSharedPreferences("juoksu" + i, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = runPref.edit();

        Float f = (float) newLength;

        editor.putFloat("matka", f);
        editor.putInt("askeleet", this.stepCount);

        //Talletetaan preferenssiin päivämäärä, jonka eteen tulee indeksi
        editor.putString("päivä", i+LocalDate.now().toString());

        editor.commit();

        i++;

        SharedPreferences.Editor indexEditor = index.edit();

        indexEditor.putInt("i", i);
        indexEditor.commit();


        //Pysäyttää handlerin
        handler.removeCallbacks(runnable);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //Käyttöliittymän päivitys/asetus
    private void updateUI () {
        TextView tv1 = findViewById(R.id.travelLengthView);
        tv1.setText(newLength + "m");
        TextView tv = findViewById(R.id.stepView);
        tv.setText(this.stepCount + "");
    }
}