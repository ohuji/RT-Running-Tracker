package project.rt_running_tracker;

/** Historia luokan luonti
 * @Janne Hakkarainen
 * @Juho Salomäki
 * Remy Silanto
 */
public class History {

    private int step;
    private String dateData;
    private float calories;
    private float journey;
    private String time;

    /**
     * Luo historia olion jolla on tietyt parametrit
     * @param step suorityksen askelmäärä
     * @param dateData suorituksen päivämäärä ja indeksi
     * @param calories suorituksessa poltetut kalorit
     * @param journey suorituksessa kuljettu matka metreinä
     * @param time suoritukseen kulunut aika
     */

    public History(int step, String dateData, float calories, float journey, String time) {

        this.step = step;
        this.dateData = dateData;
        this.calories = calories;
        this.journey = journey;
        this.time = time;
    }

    /**
     * Haetaan askelten määrä
     * @return askelten määrä muutetaan int:stä ja palautetaan stringinä
     */
    //Metodeilla saadaan haettua tarvittava tieto, joka näytetään HistoryDetailsActivityssä
    public String getStep() {
        return String.valueOf(this.step);
    }

    /**
     * Haetaan poltetut kalorit
     * @return kalorit muutetaan float:sta ja palautetaan stringinä
     */
    public String getCalories() {
        return String.valueOf(this.calories);
    }

    /**
     * Haetaan kuljettu matka
     * @return matka muutetaan float:sta ja palutetaan stringinä
     */
    public String getJourney() {
        return String.valueOf(this.journey);
    }

    /**
     * Haetaan kulunut aika
     * @return aika stringinä
     */
    public String getTime(){
        return this.time;
    }

    /**
     * Haetaan päivädata ja indeksi
     * @return päivä ja indeksi stringinä
     */
    public String toString(){
        return this.dateData;
    }

}
