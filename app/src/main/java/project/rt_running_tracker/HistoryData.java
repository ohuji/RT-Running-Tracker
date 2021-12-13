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

    //Metodin avulla saadaan valittu indeksi
    public History gethistoryData(int i) {
        return historyData.get(i);
    }

    //Metodi tyhjentää listan
    public void clearList (){
        this.historyData.clear();
    }

    //Metodi lisää Arraylistiin
    public void add (int step, String dateData, float calories, float journey, String time){
        History h = new History(step, dateData, calories, journey, time);
        this.historyData.add(h);
    }
}

