package org.tensorflow.lite.examples.detection;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;
public class ABOUT extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainabt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        toolbar.setTitle("OBJECT BOX");
        if(getSupportActionBar() != null){
            //Toast.makeText(getApplicationContext(), "getSupportActionBar", Toast.LENGTH_LONG).show();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        Element adsElement = new Element();
        adsElement.setTitle("TensorFlow Lite Object Detection Android App");

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .enableDarkMode(false)
                .setDescription(getString(R.string.app_description))
                .setImage(R.drawable.dummy_image)
                .addItem(new Element().setTitle("Version 1.0"))
                .addItem(adsElement)
                .addGroup("Connect with us")
                .addEmail("moinkhan3012@gmail.com")
                .addWebsite("https://www.linkedin.com/in/priyanshi-singh-259974147/")
                .addFacebook("profile.php?id=100009721847922")
                .addTwitter("iam_moinkhan")
                .addPlayStore("com.ideashower.readitlater.pro")
                .addInstagram("moin0k")
                .addGitHub("moinkhan3012")
                .addItem(getCopyRightsElement())
                .create();

        setContentView(aboutPage);
    }
    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.about_icon_copy_right);
        copyRightsElement.setAutoApplyIconTint(true);
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ABOUT.this, copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }
}
