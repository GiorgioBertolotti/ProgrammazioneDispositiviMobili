package it.unimib.quakeapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import info.androidhive.fontawesome.FontDrawable;
import it.unimib.quakeapp.Home;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;

    public static final String TAG = "it.unimib.QuakeApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_all_quakes)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        initDrawerLayout();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void initDrawerLayout() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        int[] iconsFirstGroup = {
                R.string.fa_home_solid,
                R.string.fa_search_solid,
                R.string.fa_history_solid,
                R.string.fa_project_diagram_solid
        };

        renderMenuIcons(navigationView.getMenu().getItem(0).getSubMenu(), iconsFirstGroup, true, false);

        int[] iconsSecondGroup = {
                R.string.fa_question_circle_solid,
                R.string.fa_cog_solid,
                R.string.fa_bug_solid
        };

        renderMenuIcons(navigationView.getMenu().getItem(1).getSubMenu(), iconsSecondGroup, true, false);
    }

    private void renderMenuIcons(Menu menu, int[] icons, boolean isSolid, boolean isBrand) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (!menuItem.hasSubMenu()) {
                FontDrawable drawable = new FontDrawable(this, icons[i], isSolid, isBrand);
                drawable.setTextColor(ContextCompat.getColor(this, R.color.black));
                drawable.setTextSize(22);
                menu.getItem(i).setIcon(drawable);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportActionBar().setTitle(getString(R.string.menu_home));
                navigateTo(new Home());
                break;
            case R.id.nav_all_quakes:
                getSupportActionBar().setTitle(getString(R.string.menu_earthquakes));
                navigateTo(new EarthquakeList());
                break;
            case R.id.nav_bugs:
                Intent intent = new Intent(this, BugReport.class);
                startActivity(intent);
                break;
            case R.id.nav_faq:
                Intent intentFaq = new Intent(this, FAQ.class);
                startActivity(intentFaq);
                break;
            case R.id.nav_settings:
                Intent intentSettings = new Intent(this, Settings.class);
                startActivity(intentSettings);
                break;
            case R.id.nav_seismic_network:
                getSupportActionBar().setTitle(getString(R.string.menu_seismic_network));
                navigateTo(new SeismicNetworkList());
                break;
            case R.id.nav_last:
                getSupportActionBar().setTitle(getString(R.string.menu_last));
                navigateTo(new Recent());
                break;
            default:
                break;
        }

        DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawers();
        return false;
    }

    private void navigateTo(Fragment fragment) {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStackImmediate();
        }
        ft.replace(R.id.nav_host_fragment, fragment);
        //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }
}
