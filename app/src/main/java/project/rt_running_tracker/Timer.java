package project.rt_running_tracker;

public class Timer {
    private int seconds;
    private int minutes;
    private int hours;

    public Timer()    {
        this.seconds = 0;
        this.minutes = 0;
        this.hours = 0;
    }

    public void addSecond()  {
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

    public void resetTimer()    {
        this.seconds = 0;
        this.minutes = 0;
        this.hours = 0;
    }

    public int getSeconds() {
        return this.seconds;
    }

    public int getMinutes() {
        return this.minutes;
    }

    public int getHours() {
        return this.hours;
    }

    public String toString()    {
        return this.hours + "." + this.minutes + "." + this.seconds;
    }
}
