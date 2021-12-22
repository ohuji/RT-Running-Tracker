package project.rt_running_tracker;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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

/**
 * <p>Suoritus aktiviteetti, käynnistetään kun käyttäjä painaa suorituksen aloitus nappia</p>
 *
 * @author Juho Salomäki, Janne Hakkarainen ja Remy Silanto.
 */

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

    //Muut instanssimuuttujat
    private int i;
    Timer sekunttikello = new Timer();
    private double caloriesBurned;
    private double caloriesPerMin;
    private double newCaloriesPerMin;

    /**
     * <p>onCreate lifecycle metodi.</p>
     * <p>Kun luodaan Excercise activity, kutsutaan sensorPermissions()
     * metodia että päästään käyttäjän sensoritietoihin käsiksi. Nollataan laskurit.
     * Lasketaan kalorien kulutus ja askelpituus. Asetetaan sensori. Jos ollaan saatu
     * paikannusluvat niin asetetaan kartta.</p>
     * @param savedInstanceState saadaan savedInstanceState.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        sensorPermissions();

        //Nollataan askel laskurin, matkan laskurin ja kalori laskurin arvot aina kun aktiviteetti avataan.

        this.stepCount = 0;
        this.travelLength = 0.0;
        this.caloriesBurned = 0.0;

        /*
            Asetetaan 8.5 MET kerroin ja lasketaan met * paino * 3.5 / 200,
            että saadaan kalorien kulutus.
        */

        double met = 8.5;

        SharedPreferences userPref = getSharedPreferences("SavedUserProfileData", Activity.MODE_PRIVATE);

        int weight = userPref.getInt("User weight", 0);

        this.caloriesBurned = met * weight * 3.5 / 200.0;

        /*
            Asetetaan 0.415 kerroin, koska pituus * 0.415 = askelpituus.
            Haetaan preferenssistä käyttäjän asettama pituus, minkä
            jälkeen lasketaan askelpituus.
         */

        double multiplier = 0.415;

        int height = userPref.getInt("User height", 0);

        this.stepLength = height * multiplier;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (countSensor != null) {
            this.sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        } else {
            Log.d("sensor.present", "sensor is not present");
        }

        //Pyydetään luvat käyttäjän paikantamiseen ennen kartan luomista.
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            //mMapView muuttuja tehdään MapView kartasta.
            mMapView = (MapView) findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);

            mMapView.getMapAsync(this);
        }
    }

    //Kysytään sensorien käyttöön luvat.
    public void sensorPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }
    }

    /**
     * <p>onResume() lifecycle metodi.</p>
     * <p>Rekisteröidään sensorille kuuntelija, jos getDefaultSensor ei ole null.
     * Jos paikannusluvat on saatu niin kutsutaan mMapViewin onResume() metodia.</p>
     */
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

    /**
     * <p>Tarkastetaan onko käyttäjä antanut luvat laitteen paikannukseen.</p>
     * <p>Jos luvat on annettu, käytetään setMyLocationEnabled metodia sallimaan "location layer",
     * mikä sallii laitteen hyödyntämään nykyistä sijantia.</p>
     * <p>Kartan POI pisteet otetaan pois käyttämällä json tiedostoa.</p>
     * <p>Haetaan ja asetetaan alkukoordinaatit lat ja lng muuttujille.</p>
     * <p>Asetetaan kartta käyttäjän kohdalle lat ja lng muuttujilla ja zoomataan lähelle käyttäjää.</p>
     * <p>Toistetaan drawPolyline metodia sekä pyöritetään sekunttikelloa handler loopilla</p>
     * @param googleMap saa GoogleMap:in arvokseen
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            googleMap.setMyLocationEnabled(true);

            //Haetaan map_style json tiedosto ja poistetaan POI pisteet.
            googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));

            //LocationServices avulla tallennetaan paikannustiedot muuttujaan.
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {

                /**
                 * <p>onSuccess metodia käytetään, jos onnistuttiin saamaan käyttäjän sijainti</p>
                 * <p>Asetetaan lat ja lng muuttujille arvot</p>
                 * <p>Kohdistetaan kartan kamera käyttäjään ja zoomataan lähemmäksi</p>
                 * @param location saa käyttäjän sijainnin arvokseen
                 */
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {

                        //Asetetaan lat ja lng muuttujat.
                        lat = location.getLatitude();
                        lng = location.getLongitude();

                        //Liikutetaan kamera käyttäjän kohdalle ja zoomataan lähemmäs.
                        LatLng currentLocation = new LatLng(lat, lng);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15.0f));
                    }
                }
            });
        }

        /**
         * <p>Metodi toistaa drawPolylines metodia 3 sekunnin välein
         * ja lisää sekunttikelloon sekunnin 1 sekunnin välein.</p>
         */

        handler.postDelayed(runnable = new Runnable() {
            int i = 0;
            public void run() {
                handler.postDelayed(runnable, 1000);

                sekunttikello.addSecond();

                updateUI();

                if (i >= 3) {
                    drawPolylines(googleMap);
                    i = 0;
                }
                i++;
            }
        }, 1000);
        this.mMap = googleMap;
    }

    /**
     * <p>onPause() lifecycle metodi.</p>
     * <p>Jos on paikannusluvat niin kutsutaan mMapViewin onPause() metodia.</p>
     * <p>Jos getDefaultSensor() ei ole null niin peruutetaan sensorin kuuntelijan
     * rekisteröinti.</p>
     */
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

    /**
     * <p>Metodi, jota kutsutaan kun sensorissa tapahtuu muutos</p>
     * <p>Jos parametrina saatu sensori on sama, kun tallennettu sensori, niin
     * lisätään yhdellä askelmittaria, lasketaan matka sekä kalorit per minuutti
     * ja päivitetään UI kutsumalla metodia updateUI()</p>
     * @param event saadaan event.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == this.sensor) {
            this.stepCount++;

            //Lasketaan matka ja kalorit per minuutti.

            this.travelLength = this.stepCount * this.stepLength / 100.0;
            this.caloriesPerMin = this.caloriesBurned * sekunttikello.getMinutes();

            //Muunnetaan doublet kahden desimaalin tarkaksi, käyttäen Javan BigDecimalia.

            BigDecimal bd = new BigDecimal(this.travelLength).setScale(2, RoundingMode.HALF_UP);
            BigDecimal bd2 = new BigDecimal(this.caloriesPerMin).setScale(2, RoundingMode.HALF_UP);

            newLength = bd.doubleValue();
            newCaloriesPerMin = bd2.doubleValue();

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

    /**
     * <p>Metodi piirtää Google Maps View:iin kahden koordinaatiston välille käyttäjää seuraavan reaaliaikaisen polylinen
     * hyödyntämällä onMapReady metodissa olevaa handler looppia.</p>
     * <p>Ensimmäinen koordinaatisto on aina aiemman kierroksen viimeinen koordinaatisto,
     * paitsi aivan ensimmäisellä kerralla käytetään jo aiemmin onMapReady metodissa asetettua koordinaatistoa.</p>
     * <p>Toinen koordinaatisto on tällä kierroksella saatu uusi koordinaatisto.</p>
     * <p>Tällä tavalla saadaan piirrettyä katkeamaton polyline käyttäjän juoksusta GoogleMaps:iin.</p>
     * @param googlemap saa GoogleMaps:in arvokseen
     */

    private void drawPolylines(GoogleMap googlemap) {

        /*
        Tarkastetaan onko käyttäjä antanut luvat paikannukseen.
        Mikäli on haetaan käyttäjän viimeisin sijainti.
        */

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            //LocationServices avulla tallennetaan paikannustiedot muuttujaan.
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {

                /**
                 * <p>onSuccess metodia käytetään, jos onnistuttiin saamaan käyttäjän sijainti.</p>
                 * <p>Metodissa tapahtuu itse polylinen piirto ja sen ulkonäön asettaminen.</p>
                 * <p>Metodissa asetetaan myös lat ja lng muuttujien arvot seuraavaa kierrosta varten.</p>
                 * @param location saa käyttäjän sijainnin arvokseen.
                 */

                @Override
                public void onSuccess(Location location) {

                    //Haettiin käyttäjän viimeisin sijainti ja nyt tarkistetaan vielä löytyihän se if lauseella.
                    if (location != null) {

                        //Piirretään polyline aiemmin tallennettujen lat ja lng muuttujien arvojen ja nykyisten koordinaattien välille.
                        Polyline polyline1 = googlemap.addPolyline(new PolylineOptions()
                                .add(new LatLng(lat, lng),
                                        new LatLng(location.getLatitude(), location.getLongitude())));

                        //Asettaa polylinen värin, leveyden ja päätyjen muodon.
                        polyline1.setColor(ContextCompat.getColor(context, R.color.teal_200));
                        polyline1.setWidth(20);
                        polyline1.setEndCap(new RoundCap());
                        polyline1.setStartCap(new RoundCap());

                        polyline1.setTag("A");

                        //Tallennetaan viimeisimmät koordinaatit muuttujiin.
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                    }
                }
            });
        }
    }

    /**
     * <p>Stop napin painamis metodi</p>
     * <p>Luodaan callback metodista onSnapshotReady(),
     * jota kutsutaan vasta kun kartta on latautunut. Kun snapshot on otettu
     * lisätään matka, kalorit, askeleet ja kesto preferensseihin. Myös indeksiä,
     * jonka perusteella kuva, sekä preferenssi tiedosto nimetään korotetaan yhdellä
     * ja lisätään takaisin preferensseihin. Lopussa nollataan sekuntikello, pysäytetään handler looppi ja
     * käynnistetään main aktiviteetti.</p>
     * @param v saadaan view.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onStopButtonClick(View v) {
        final GoogleMap.SnapshotReadyCallback snapshotReadyCallback = new GoogleMap.SnapshotReadyCallback() {
            Bitmap bitmap;

            /**
             * <p>Snapshot metodi.</p>
             * <p>Jos indexi preferenssi löytyy niin käytetään sen arvoa, jos taas ei löydy
             * niin luodaan uusi preferenssi tiedosto. Luodaan kuva PNG muodossa.
             * Virheen tapahtuessa printataan stacktrace.</p>
             * @param snap saadaan bitmap.
             */
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
            /**
             * <p>Kun kartta on latautunut otetaan snapshotti.</p>
             */
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
        Float f2 = (float) newCaloriesPerMin;

        editor.putFloat("matka", f);
        editor.putFloat("kalorit", f2);
        editor.putInt("askeleet", this.stepCount);
        editor.putString("harjoituksen kesto", sekunttikello.toString());

        //Talletetaan preferenssiin päivämäärä, jonka eteen tulee indeksi.
        editor.putString("päivä", i+LocalDate.now().toString());

        editor.commit();

        i++;

        SharedPreferences.Editor indexEditor = index.edit();

        indexEditor.putInt("i", i);
        indexEditor.commit();


        //Pysäyttää handlerin.
        handler.removeCallbacks(runnable);

        //Nollaa laskurin.
        sekunttikello.resetTimer();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * <p>Käyttöliittymän päivitys/asetus.</p>
     */
    private void updateUI () {
        TextView tv = findViewById(R.id.travelLengthValueView);
        tv.setText(newLength + "m");
        TextView tv1 = findViewById(R.id.stepsValueView);
        tv1.setText(String.valueOf(this.stepCount));
        TextView tv3 = findViewById(R.id.timerView);
        tv3.setText(sekunttikello.toString());
        TextView tv4 = findViewById(R.id.caloriesValueView);
        tv4.setText(String.valueOf(this.newCaloriesPerMin));
    }
}