package project.rt_running_tracker;

/** Historia datan luonti
 * @Janne Hakkarainen
 * @Juho Salomäki
 * Remy Silanto
 */
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

    /**
     * Haetaan kyseisen olion indeksi
     * @param i osion indeksi
     * @return saadaan indeksi, jonka avulla voidaan hakea muilla metodeilla oikea data
     */
    //Metodin avulla saadaan valittu indeksi
    public History gethistoryData(int i) {
        return historyData.get(i);
    }

    /**
     * Tyhjennetään olemassa oleva lista, jotta ei synny duplikaatti listoja
     */
    //Metodi tyhjentää listan
    public void clearList (){
        this.historyData.clear();
    }

    /**
     * Lisätään arraylistiin oliona
     * @param step askeleet
     * @param dateData pvm + indeksi
     * @param calories poltetut kalorit
     * @param journey kuljettu matka
     * @param time kulunut aika
     */
    //Metodi lisää Arraylistiin
    public void add (int step, String dateData, float calories, float journey, String time){
        History h = new History(step, dateData, calories, journey, time);
        this.historyData.add(h);
    }
}

