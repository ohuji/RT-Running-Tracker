package project.rt_running_tracker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

public class HistoryData {

    private static final HistoryData ourInstance = new HistoryData();
    private ArrayList<History> historyData;

    public static HistoryData getInstance() {
        return ourInstance;
    }

    public HistoryData() {
        historyData = new ArrayList<History>();
    }

    public ArrayList<History> getHistoryData() {
        return historyData;
    }

    public History gethistoryData(int i) {
        return historyData.get(i);
    }

    public void add (int step, String dateData, float calories, float journey){
        History h = new History(step, dateData, calories, journey);
        this.historyData.add(h);
    }
}

