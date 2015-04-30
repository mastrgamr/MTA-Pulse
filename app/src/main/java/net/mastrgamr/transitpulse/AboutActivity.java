package net.mastrgamr.transitpulse;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class AboutActivity extends ActionBarActivity {

    Button licenseButton;

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
    }
}
