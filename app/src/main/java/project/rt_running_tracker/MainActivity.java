package project.rt_running_tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
}