package project.rt_running_tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private int savedIndex;
    private int savedChecker;
    private int savedStep;
    private String savedDateData;
    private float savedCalories;
    private float savedJourney;

    //private int checker;

    //public static Context contextOfApplication;

    public static final String EXTRA = "HistoryDetailsActivityyn";
    private static final String TAG = "tsuptsup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getSavedIndex();
        //HistoryData myClass = new HistoryData();
        //contextOfApplication = getApplicationContext();
        getExcerciseData();

        //checker = 0;

        // ArrayList<History> historyData = new ArrayList<History>();
        //getSavedDateData();


        ListView lv = (ListView) findViewById(R.id.lvHistoryData);

        lv.setAdapter(new ArrayAdapter<History>(
                this,
                android.R.layout.simple_list_item_1,
                HistoryData.getInstance().getHistoryData())
        );

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick(" + i + ")");
                Intent nextActivity = new Intent(HistoryActivity.this, HistoryDetailsActivity.class);
                nextActivity.putExtra(EXTRA, i);
                startActivity(nextActivity);
            }
        });
    }

    public String getSavedDateData() {
        System.out.println(savedDateData);
        return savedDateData;
    }

    public void getSavedIndex() {
        SharedPreferences prefGet = getSharedPreferences("index", Activity.MODE_PRIVATE);
        savedIndex = prefGet.getInt("i", 0);
    }

    public void getSavedChecker() {
        SharedPreferences prefGet = getSharedPreferences("SavedHistoryChecker", Activity.MODE_PRIVATE);
        savedChecker = prefGet.getInt("checker", 0);
    }

    public void getExcerciseData() {

        getSavedChecker();
        getSavedIndex();

        for (int r = 0; r <= savedIndex; r++) {
            SharedPreferences prefGet1 = getSharedPreferences("juoksu" + r, Activity.MODE_PRIVATE);
            savedStep = prefGet1.getInt("askeleet", 0);
            savedCalories = prefGet1.getFloat("kalorit", 0);
            savedJourney = prefGet1.getFloat("matka", 0);
            savedDateData = prefGet1.getString("päivä", "1.1.0001");




            if (savedChecker < savedIndex) {
                savedChecker++;
                HistoryData.getInstance().add(savedStep, savedDateData, savedCalories, savedJourney);

                SharedPreferences prefPut = getSharedPreferences("SavedHistoryChecker", Activity.MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = prefPut.edit();
                prefEditor.putInt("checker", savedChecker);
                prefEditor.commit();
                
            }

        }
    }
}

/*
 /*
    public static Context getContextOfApplication(){
        return contextOfApplication;
    }
//Testi tallentaa päivä preferenssiin
        LocalDate paiva = LocalDate.now();
        editor.putString("päivä", i+paiva.toString());
 */

