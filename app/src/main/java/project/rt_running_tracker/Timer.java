package project.rt_running_tracker;

public class Timer {
    private int seconds;
    private int minutes;
    private int hours;

    /**
     * Asetetaan oliomuuttujille seconds, minutes ja hours alkuarvot.
     */

    public Timer()    {
        this.seconds = 0;
        this.minutes = 0;
        this.hours = 0;
    }

    /**
     * Metodi lisää yhden oliomuuttujaan seconds ja tarkastaa onko sekunteja kertynyt 60,
     * mikäli on nollataan sekuntien määrä ja lisätään yksi minuutti oliomuuttujaan minutes.
     * Tarkistetaan vielä, onko minuutteja kertynyt 60,
     * mikäli on nollataan minuutit ja lisätään yksi oliomuuttujaan hours eli tunteihin.
     */

    protected void addSecond()  {
        this.seconds++;

        if (this.seconds >= 60) {
            this.minutes++;
            this.seconds = 0;
        }
        if (this.minutes >= 60) {
            this.hours++;
            this.minutes = 0;
        }
    }

    /**
     * @return minutes oliomuuttujan arvon.
     */

    public int getMinutes() {
        return minutes;
    }

    /**
     * Metodi nollaa kaikki oliomuuttujat.
     */

    protected void resetTimer()    {
        this.seconds = 0;
        this.minutes = 0;
        this.hours = 0;
    }

    /**
     * @return merkkijonona oliomuuttujien arvot muodossa: "hours.minutes.seconds".
     */

    public String toString()    {
        return this.hours + "." + this.minutes + "." + this.seconds;
    }
}
