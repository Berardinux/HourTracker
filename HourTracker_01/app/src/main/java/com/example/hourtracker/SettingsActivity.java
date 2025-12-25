package com.example.hourtracker;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.hourtracker.data.SettingsStore;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

// Settings activity

public class SettingsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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

        // <Store user settings>

        SettingsStore settings = new SettingsStore(this);
        // Load saved values into EditTexts.
        EditText rateET = findViewById(R.id.settings_edittext_0);
        EditText breakfastET = findViewById(R.id.settings_edittext_1);
        EditText lunchET = findViewById(R.id.settings_edittext_2);
        EditText dinnerET = findViewById(R.id.settings_edittext_3);

        setIfNotZero(rateET, settings.getRate());
        setIfNotZero(breakfastET, settings.getBreakfast());
        setIfNotZero(lunchET, settings.getLunch());
        setIfNotZero(dinnerET, settings.getDinner());

        // Setup click listener for the save button.
        Button saveBtn = findViewById(R.id.settings_button_0);
        saveBtn.setOnClickListener(v -> {
            settings.saveRate(safeParse(rateET));
            settings.saveBreakfast(safeParse(breakfastET));
            settings.saveLunch(safeParse(lunchET));
            settings.saveDinner(safeParse(dinnerET));

            Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show();
        });

        // </Store user settings>


        // Handle menu item clicks
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


        // ========== SETTINGS ITEM ==========
        MenuItem settingsItem = menu.findItem(R.id.menu_settings);
        settingsItem.setChecked(true);

        SpannableString oneText = new SpannableString(settingsItem.getTitle());
        oneText.setSpan(new StyleSpan(Typeface.BOLD), 0, oneText.length(), 0);
        settingsItem.setTitle(oneText);
    }

    // <Store user settings methods>
    private void setIfNotZero(EditText et, float value) {
        if (value != 0f) et.setText(String.valueOf(value));
        else et.setText("");
    }

    private float safeParse(EditText et) {
        String text = et.getText().toString().trim();
        if (text.isEmpty()) return 0f;
        try {
            return Float.parseFloat(text);
        } catch (Exception e) {
            return 0f;
        }
    }
    // </Store user settings methods>


}
