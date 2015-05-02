package net.mastrgamr.transitpulse;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        ServiceStatusInfoFragment.OnStatusClickedListener {

    private final String LOG_TAG = getClass().getSimpleName();
    private static final String STATUS_TEXT = "statusText";
    private int backPresses = 0;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        fragmentManager = getFragmentManager();
        switch (position) {
            case 0:
                backPresses = 0;
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new ServiceStatusFragment().newInstance(position + 1))
                        .commit();
                break;
            case 1:
                backPresses = 0;
                Intent i = new Intent(this, TripActivity.class);
                startActivity(i);
                break;
            default:
                Log.e(LOG_TAG, "Something fucked up with these fragments!");
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
        if (id == R.id.action_about) {
            Intent i = new Intent(this, AboutActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        fragmentManager.popBackStack();

        switch (backPresses) {
            case 0:
                if (fragmentManager.getBackStackEntryCount() == 0) {
                    Toast.makeText(this, "Are you sure you want to exit?", Toast.LENGTH_SHORT).show();
                    backPresses++;
                }
                break;
            case 1:
                super.onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void onStatusClicked(String status) {
        ServiceStatusInfoFragment fragment = ServiceStatusInfoFragment.newInstance("HERRoOoOO");

        Bundle sendArgs = new Bundle();
        sendArgs.putString(STATUS_TEXT, status);
        fragment.setArguments(sendArgs);
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
