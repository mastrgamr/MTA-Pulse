package net.mastrgamr.transitpulse;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class AboutActivity extends ActionBarActivity {

    Button licenseButton;
    TextView appVersionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        licenseButton = (Button) findViewById(R.id.licenses);
        licenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AboutActivity.this, LicenseActivity.class);
                startActivity(i);
            }
        });

        appVersionText = (TextView) findViewById(R.id.appVersionText);
        String version;
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version = pInfo.versionName;
        appVersionText.setText("Build Version: " + version + "- [ALPHA]");
    }
}
