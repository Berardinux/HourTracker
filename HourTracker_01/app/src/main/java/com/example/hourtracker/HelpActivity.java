package com.example.hourtracker;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class HelpActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // <Navigation Drawer setup>
        // Set hamburger icon behavior.
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        // spacer for camera.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbar), (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(0, topInset, 0, 0);
            return insets;
        });
        updateNavdrawer();
        // </Navigation Drawer setup>

        // Handle menu item clicks.
        navigationView.setNavigationItemSelectedListener(item -> {

            drawerLayout.closeDrawer(GravityCompat.START);

            int id = item.getItemId();
            if (id == R.id.menu_main) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
            } else if (id == R.id.menu_calendar) {
                startActivity(new Intent(this, CalendarActivity.class));
                overridePendingTransition(0, 0);
            } else if (id == R.id.menu_ytd) {
                startActivity(new Intent(this, YtdActivity.class));
                overridePendingTransition(0, 0);
            } else if (id == R.id.menu_search) {
                startActivity(new Intent(this, SearchActivity.class));
                overridePendingTransition(0, 0);
            } else if (id == R.id.menu_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                overridePendingTransition(0, 0);
            } else if (id == R.id.menu_ie) {
                startActivity(new Intent(this, ImportExportActivity.class));
                overridePendingTransition(0, 0);
            } else if (id == R.id.menu_help) {
                startActivity(new Intent(this, HelpActivity.class));
                overridePendingTransition(0, 0);
            }

            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNavdrawer();  // runs when returning via Back or resuming
    }

    public void updateNavdrawer () {
        Menu menu = navigationView.getMenu();

        // ========== HELP ITEM ==========
        MenuItem helpItem = menu.findItem(R.id.menu_help);
        helpItem.setChecked(true);

        SpannableString mainText = new SpannableString(helpItem.getTitle());
        mainText.setSpan(new StyleSpan(Typeface.BOLD), 0, mainText.length(), 0);
        helpItem.setTitle(mainText);
    }

}
