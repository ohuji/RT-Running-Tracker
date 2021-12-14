package project.rt_running_tracker;

import androidx.activity.result.ActivityResultCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;

/**
 * Main aktiviteeti, joka käynnistetään, kun sovellus käynnistyy.
 *
 * @author Juho Salomäki, Janne Hakkarainen ja Remy Silanto
 */

public class MainActivity extends AppCompatActivity {

    /**
     * <p>onCreate() lifecycle metodi.</p>
     * <p>Kun luodaan mainActivity,
     * kutsutaan mapPermissions() metodia käyttäjän
     * paikannustietojen saantiin.</p>
     * @param savedInstanceState savedInstanceState.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapPermissions();
    }

    /**
     * <p>Start napin painamis metodi.</p>
     * <p>Metodissa katsotaan löytyykö preferenssi tiedosto
     * , jos löytyy käynnistetään suoritus aktiviteetti, taas jos ei
     * löydy niin käynnistetään profiilin luonti aktiviteetti.</p>
     * @param v annetaan viewi.
     */
    public void onStartClick(View v) {
        File pref = new File("/data/data/project.rt_running_tracker/shared_prefs/SavedUserProfileData.xml");

        Intent intent = new Intent(this, ExerciseActivity.class);
        Intent profIntent = new Intent(this, ProfileActivity.class);

        if (pref.exists()) {
            startActivity(intent);
        } else {
            startActivity(profIntent);
        }
    }

    /**
     * <p>Historia napin painamis metodi.</p>
     * <p>Kun nappia painetaan käynnistetään historia
     * aktiviteetti.</p>
     * @param v annetaan view.
     */
    public void onHistoryClick(View v) {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    /**
     * <p>Profiili napin painamis metodi.</p>
     * <p>Kun nappia painetaan käynnistetään profiili
     * aktiviteetti.</p>
     * @param v annetaan view.
     */
    public void onProfileClick(View v) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    /**
     * <p>Metodi kysyy luvat paikannustietoihin, jos ei olla annettu.</p>
     */
    public void mapPermissions() {
        //Kysytään tarkka lokaatio.
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }

        //Kysytään epätarkka lokaatio.
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        //Kysytään voidaanko käyttää lokaatiota taustalla.
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 101);
        }
    }

}