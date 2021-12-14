package project.rt_running_tracker;

/** Profiilin luonti
 * @Janne Hakkarainen
 * @Juho Salomäki
 * Remy Silanto
 */

public class ProfileData {

    private String userName;
    private int userWeight;
    private int userHeight;
    private String userGender;

    /**
     * Luo profiilin jossa on kyseiset parametrit
     * @param userName käyttäjän profiilinimi
     * @param userWeight käyttäjän paino
     * @param userHeight käyttäjän pituus
     * @param userGender käyttäjän sukupuoli
     */
    public ProfileData(String userName, int userWeight, int userHeight, String userGender) {

        this.userName = userName;
        this.userWeight = userWeight;
        this.userHeight = userHeight;
        this.userGender = userGender;

    }

    /**
     * Haetaan käyttäjän nimi
     * @return stringinä käyttäjän nimen
     */
    //Luotu metodit joilla saadaan haettua käyttäjän syöttämät tiedot
    public String getUserName(){
        return this.userName;
    }

    /**
     * Haetaan käyttäjän paino
     * @return int käyttäjän paino
     */
    public int getUserWeight(){
        return this.userWeight;
    }

    /**
     * Haetaan käyttäjän pituus
     * @return int käyttäjän pituus
     */
    public int getUserHeight(){
        return this.userHeight;
    }

    /**
     * Haetaan käyttäjän sukupuoli
     * @return stringinä käyttäjän sukupuoli
     */
    public String getUserGender(){
        return this.userGender;
    }

}
