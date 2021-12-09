package project.rt_running_tracker;

public class HistoryData {


    private int step;
    private double dateData;
    private double calories;
    private double journey;
    private String mapImage;

    public HistoryData(int step, double dateData, double calories, double journey, String mapImage) {

        this.step = step;
        this.dateData = dateData;
        this.calories = calories;
        this.journey = journey;
        this.mapImage = mapImage;
    }


    public double getDateData(){
        return this.dateData;
    }

}
