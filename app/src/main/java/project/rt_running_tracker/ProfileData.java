package project.rt_running_tracker;

public class ProfileData {

    private String userName;
    private int userWeight;
    private int userHeight;
    private String userGender;

    public ProfileData(String userName, int userWeight, int userHeight, String userGender) {

        this.userName = userName;
        this.userWeight = userWeight;
        this.userHeight = userHeight;
        this.userGender = userGender;
    }


    public String getUserName(){
        return this.userName;
    }

    public int getUserWeight(){
        return this.userWeight;
    }

    public int getUserHeight(){
        return this.userHeight;
    }

    public String getUserGender(){
        return this.userGender;
    }

}
