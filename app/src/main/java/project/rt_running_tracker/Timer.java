package project.rt_running_tracker;

public class Timer {
    private int sekunnit;
    private int minuutit;
    private int tunnit;

    public Timer(int sekunnit, int minuutit, int tunnit)    {
        this.sekunnit = sekunnit;
        this.minuutit = minuutit;
        this.tunnit = tunnit;
    }

    public int getSekunnit() {
        return sekunnit;
    }

    public int getMinuutit() {
        return minuutit;
    }

    public int getTunnit() {
        return tunnit;
    }

    public void setSekunnit(int sekunnit) {
        this.sekunnit = sekunnit;
    }

    public void setMinuutit(int minuutit) {
        this.minuutit = minuutit;
    }

    public void setTunnit(int tunnit) {
        this.tunnit = tunnit;
    }

    public String toString()    {
        return this.tunnit + " tuntia, " + this.minuutit + " minuuttia ja" + this.sekunnit + " sekuntia";
    }
}
