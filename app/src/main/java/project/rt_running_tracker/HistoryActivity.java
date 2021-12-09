package project.rt_running_tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HistoryActivity extends AppCompatActivity {

    public static final String EXTRA = "hakkarainen.harjoitus6.nimi";
    private static final String TAG = "tsuptsup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
/*
        ListView lv = (ListView) findViewById(R.id.lvHistoryData);

        lv.setAdapter(new ArrayAdapter<HistoryData>(
                this,
                android.R.layout.simple_list_item_1,

                //HistoryData.
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

 */
}}