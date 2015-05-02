package net.mastrgamr.transitpulse;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class LicenseActivity extends ActionBarActivity {

    private Button facebookLicense;
    private Button httpLicense;
    private Button csvLicense;
    private Button simpleLicense;
    private Button stikkyLicense;
    private Button aospLicense;
    private Button playServicesLicense;
    private Button protoLicense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        facebookLicense = (Button) findViewById(R.id.button_facebook_shimmer);
        facebookLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDialog(0);
            }
        });

        httpLicense = (Button) findViewById(R.id.button_http_request);
        httpLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDialog(1);
            }
        });

        csvLicense = (Button) findViewById(R.id.button_commons_csv);
        csvLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDialog(2);
            }
        });

        simpleLicense = (Button) findViewById(R.id.button_simple_xml);
        simpleLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDialog(3);
            }
        });

        stikkyLicense = (Button) findViewById(R.id.button_stikky_header);
        stikkyLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDialog(4);
            }
        });

        aospLicense = (Button) findViewById(R.id.button_android_open_source);
        aospLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDialog(5);
            }
        });

        playServicesLicense = (Button) findViewById(R.id.button_google_play);
        playServicesLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDialog(6);
            }
        });

        protoLicense = (Button) findViewById(R.id.button_protocol_buffers);
        protoLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDialog(7);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_license, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void displayDialog(int source) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (source) {
            case 0:
                builder.setMessage(R.string.facebook_shimmer_license)
                        .setTitle(R.string.facebook_shimmer);
                builder.setNeutralButton("Thank you Facebook!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                break;
            case 1:
                builder.setMessage(R.string.http_request_license)
                        .setTitle(R.string.http_request);
                builder.setNeutralButton("Thank you kevinsawicki!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                break;
            case 2:
                builder.setMessage(R.string.commons_csv_license)
                        .setTitle(R.string.commons_csv);
                builder.setNeutralButton("Thank you Apache!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                break;
            case 3:
                builder.setMessage(R.string.simple_xml_license)
                        .setTitle(R.string.simple_xml);
                builder.setNeutralButton("Thank you Apache!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                break;
            case 4:
                builder.setMessage(R.string.stikky_header_license)
                        .setTitle(R.string.stikky_header);
                builder.setNeutralButton("Thank you carlonzo!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                break;
            case 5:
                builder.setMessage(R.string.android_open_source_license)
                        .setTitle(R.string.android_open_source);
                builder.setNeutralButton("Thank you AOSP!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                break;
            case 6:
                builder.setMessage("TODO")
                        .setTitle(R.string.google_play);
                builder.setNeutralButton("Thank you Google!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                break;
            case 7:
                builder.setMessage(R.string.protocol_buffers_license)
                        .setTitle(R.string.protocol_buffers);
                builder.setNeutralButton("Thank you Google!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                break;
            default:
                break;
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
