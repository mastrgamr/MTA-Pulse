package net.mastrgamr.transitpulse;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class TripActivity extends ActionBarActivity {
    TripFragment tf;
    SlidingMenu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);

        tf = new TripFragment();
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, tf)
                    .commit();
        }

        //Sliding menu options
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setFadeDegree(0.35f);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.slideoutmenu);
    }

    public void showSetingsFragment() {
        //NOT efficient as this assumes backstack will have nothing in it
        //TODO: make abstract so it works from any view
        if(getFragmentManager().getBackStackEntryCount() == 0) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new SettingsFragment())
                    .hide(tf)
                    .addToBackStack("TripFragment")
                    .commit();
        }
        menu.showContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip, menu);
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
            Toast.makeText(this, "No settings yet!", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        if (tf.isVisible()) {
            if (tf.tripListAdapter != null) {
                tf.tripListAdapter.setStationSelected(false, 0);
                tf.setSubwayListColumns(1);
            }
        }

        if(getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
    }
}
