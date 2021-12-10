package project.rt_running_tracker;

public class History {

    private int step;
    private String dateData;
    private float calories;
    private float journey;

    public History(int step, String dateData, float calories, float journey) {

        this.step = step;
        this.dateData = dateData;
        this.calories = calories;
        this.journey = journey;
    }

    public String getStep() {
        return String.valueOf(this.step);
    }

    public String getCalories() {
        return String.valueOf(this.calories);
    }

    public String getJourney() {
        return String.valueOf(this.journey);
    }

    public String toString(){
        return this.dateData;
    }
}
