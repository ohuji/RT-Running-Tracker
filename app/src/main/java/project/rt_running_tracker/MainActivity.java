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

public class MainActivity extends AppCompatActivity {

    //private int checker = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissions();

        //Lisätty checker, joka tallennetaan preferenssiin
       /*
        SharedPreferences prefPut = getSharedPreferences("SavedHistoryChecker", Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefPut.edit();
        prefEditor.putInt("checker", checker);
        prefEditor.commit();

        */
    }

    //Nappien painellus metodit.

    public void onStartClick(View v) {
        File pref = new File("/data/data/project.rt_running_tracker/shared_prefs/SavedUserProfileData.xml");

        Intent intent = new Intent(this, ExerciseActivity.class);
        Intent profIntent = new Intent(this, ProfileActivity.class);

        /*
            Katsotaan löytyykö preferenssi tiedosto, jos ei löydy
            aloitetaan profiilin luonti aktiviteetti. Jos löytyy
            aloitetaan suoritus aktiviteetti.
        */

        if (pref.exists()) {
            startActivity(intent);
        } else {
            startActivity(profIntent);
        }
    }

    public void onHistoryClick(View v) {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    public void onProfileClick(View v) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void permissions()  {

        //Kysytään luvat käyttäjän askelien laskemiseen ja paikantamiseen

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) !=  PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.ACTIVITY_RECOGNITION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    }, 101);
        }
    }
}