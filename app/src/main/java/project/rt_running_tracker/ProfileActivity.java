package project.rt_running_tracker;

/** Profiin luonti aktiviteetti
 * @Janne Hakkarainen
 * @Juho Salomäki
 * Remy Silanto
 */

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    private ProfileData newProfile;

    private String savedUserName;
    private int savedUserWeight;
    private int savedUserHeight;
    private String savedUserGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getProfileData();

        /**
         * Etsitään id:n avulla oikea tekstikenttä johon laitetaan muuttujan arvo
         */
        //Haetaan tekstinsyöttökenttään tiedot preferensseistä
        TextView settii = findViewById(R.id.editUserName);
        settii.setText(savedUserName);

        TextView settii2 = findViewById(R.id.editUserWeight);
        settii2.setText(String.valueOf(savedUserWeight));

        TextView settii3 = findViewById(R.id.editUserHeight);
        settii3.setText(String.valueOf(savedUserHeight));

        genderSelected();

        /**
         * Tarkastetaan mikä sukupuoli on muuttujassa savedUserGender ja sen perusteella
         * asetetaan radiobutton painetuksi
         */
        //Tarkastetaan mikä sukupuoli on preferenssissä ja asetaan radiobutton valituksi sen mukaan
        if(savedUserGender.equals("Male")){
            RadioButton maleRadioButton = (RadioButton) findViewById(R.id.rbMale);
            maleRadioButton.setChecked(true);
        }
        if(savedUserGender.equals("Female")){
            RadioButton femaleRadioButton = (RadioButton) findViewById(R.id.rbFemale);
            femaleRadioButton.setChecked(true);
        }
        if(savedUserGender.equals("Other")){
            RadioButton otherRadioButton = (RadioButton) findViewById(R.id.rbOther);
            otherRadioButton.setChecked(true);
        }
    }

    /**
     * radio painike kuuntelija. Toteuttaa metodin genderSelected()
     * @param view
     */
    public void onRadioButtonClicked(View view) {
        genderSelected();
    }

    /**
     * Save painiketta painettaessa toteutetaan metodi saveProfile()
     * @param view
     */
    public void bSave(View view) {
        saveProfile();
    }

    /**
     * Metodi saveProfile() id:n avulla saadaan tekstikenttään syötetyt tiedot ja muutetaan ne tarvittaessa stringiksi.
     * Luodaan uusi profiili
     * Tallennetaan preferenssiin saadut tiedot
     */
    //Tallennetaan syötekentässä oleva tieto preferenssiin
    public void saveProfile() {

        EditText editedName = (EditText) findViewById(R.id.editUserName);
        String name = editedName.getText().toString();

        EditText editedWeight = (EditText) findViewById(R.id.editUserWeight);
        int weight = Integer.parseInt(editedWeight.getText().toString());

        EditText editedHeight = (EditText) findViewById(R.id.editUserHeight);
        int height = Integer.parseInt(editedHeight.getText().toString());

        String gender = genderSelected();

        newProfile = new ProfileData(name, weight, height, gender);

        SharedPreferences prefPut = getSharedPreferences("SavedUserProfileData", Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefPut.edit();
        prefEditor.putString("User name", newProfile.getUserName());
        prefEditor.putInt("User weight", newProfile.getUserWeight());
        prefEditor.putInt("User height", newProfile.getUserHeight());
        prefEditor.putString("User gender", newProfile.getUserGender());
        prefEditor.commit();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Haetaan preferenssistä profiili data jotta se voidaan asettaa tekstikenttiin
     */
    //Haetaan preferenssissä olevan profiilin tiedot
    public void getProfileData() {
        SharedPreferences prefGet = getSharedPreferences("SavedUserProfileData" ,Activity.MODE_PRIVATE);
        savedUserName = prefGet.getString("User name", "profilename");
        savedUserWeight = prefGet.getInt("User weight", 0);
        savedUserHeight = prefGet.getInt("User height", 0);
        savedUserGender = prefGet.getString("User gender", "Other");
    }

    /**
     * radiobutton id:n avulla saadaan selville mikä on painettuna ja palautetaan arvo sen mukaan.
     * @return palauttaa joko arvon Male, Female, Other
     */
    //Katsotaan mikä radiobutton on painettuna ja palautetaan sen perusteella sukupuoli Stringinä, joka voidaan tallentaa preferenssiin
    public String genderSelected() {
        RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroupGender);
        int id = rg.getCheckedRadioButtonId();
        if (id == R.id.rbMale) {
            return "Male";
        }
        if (id == R.id.rbFemale) {
            return "Female";
        } else {
            return "Other";
        }
    }
}