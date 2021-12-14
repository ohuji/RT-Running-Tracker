package project.rt_running_tracker;
/** HistoryDetails aktiviteetti
 * @Janne Hakkarainen
 * @Juho Salomäki
 * Remy Silanto
 */

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class HistoryDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_details);

        /**
         * Intentin avulla saadaan edellisessä aktiviteetissa luotu indeki.
         * Luodaan muuttuja datei ja indeksiä hyödyntäen saadaan haettua oikea pvm+indeksi
         */
        Bundle b = getIntent().getExtras();
        int i = b.getInt(HistoryActivity.EXTRA);
        String datei = HistoryData.getInstance().gethistoryData(i).toString();

        /**
         * Jos kuvan indeksi on alle kymmenen käytetään alla olevaa if lausetta jolla saadaan oikea indeksi kuvalle.
         * Jos indeksi on yli kymmenen tai enemmän saadaan oikea kuva haettua muuttujalla datei
         */
        if (i < 10) {
            int j = Integer.parseInt(datei.substring(0, 1));
            String a = datei.substring(1);
            j++;
            String s = j + a;

            /**
             * Kuvakenttään asetaan oikeaa juoksua vastaava kuva
             */
            //Kuvakenttään asetaan oikeaa juoksua vastaava kuva
            File mapImage = new File("/data/data/project.rt_running_tracker/files/image" + s + ".png");

        if(mapImage.exists()) {

            Bitmap bitmap = BitmapFactory.decodeFile(mapImage.getAbsolutePath());

            ImageView myImage = (ImageView) findViewById(R.id.ivMap);

            myImage.setImageBitmap(bitmap);
        }
        } else {
            File mapImage2 = new File("/data/data/project.rt_running_tracker/files/image" + datei + ".png");

            if(mapImage2.exists()) {

                Bitmap bitmap = BitmapFactory.decodeFile(mapImage2.getAbsolutePath());

                ImageView myImage = (ImageView) findViewById(R.id.ivMap);

                myImage.setImageBitmap(bitmap);
            }
        }

        /**
         * Tekstikenttään asetetaan indeksiä vastaavan juoksun tiedot
         */
        //Tekstikenttään asetetaan indeksiä vastaavan juoksun tiedot
        ((TextView)findViewById(R.id.tvSavedSteps))
                .setText(HistoryData.getInstance().gethistoryData(i).getStep());

        ((TextView)findViewById(R.id.tvSavedJourney))
                .setText(HistoryData.getInstance().gethistoryData(i).getJourney()+"m");

        ((TextView)findViewById(R.id.tvSavedCalories))
                .setText(HistoryData.getInstance().gethistoryData(i).getCalories());

        ((TextView)findViewById(R.id.tvSavedTime))
                .setText(HistoryData.getInstance().gethistoryData(i).getTime());

    }
}
