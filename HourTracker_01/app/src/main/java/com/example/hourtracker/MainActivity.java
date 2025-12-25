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
import com.example.hourtracker.data.WorkLogStore;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // <By default set the date to today's date>
        EditText dateET = findViewById(R.id.main_edittext_0);

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        dateET.setText(today);
        // </By default set the date to today's date>

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


        // <Store Work Log>
        EditText x1ET = findViewById(R.id.main_edittext_1);
        EditText x1_5ET = findViewById(R.id.main_edittext_2);
        EditText x2ET = findViewById(R.id.main_edittext_3);
        EditText x2_5ET = findViewById(R.id.main_edittext_4);
        EditText breakfastET = findViewById(R.id.main_edittext_5);
        EditText lunchET = findViewById(R.id.main_edittext_6);
        EditText dinnerET = findViewById(R.id.main_edittext_7);
        EditText vacationET = findViewById(R.id.main_edittext_8);
        EditText restET = findViewById(R.id.main_edittext_9);

        Button addBtn = findViewById(R.id.main_button_0);

        // Setup click listener for the add time button.
        addBtn.setOnClickListener(v -> {
            WorkLogStore store = new WorkLogStore(this);
            WorkLogStore.WorkEntry entry = new WorkLogStore.WorkEntry();

            // Check if the date is in the right format
            String dateInput = dateET.getText().toString().trim();

            if (!isValidDate(dateInput)) {
                Toast.makeText(this, "Invalid date formate. Example: 2030-01-30", Toast.LENGTH_SHORT).show();
                return;
            }

            entry.date = dateInput;
            entry.x1Hours = safeParse(x1ET);
            entry.x1_5Hours = safeParse(x1_5ET);
            entry.x2Hours = safeParse(x2ET);
            entry.x2_5Hours = safeParse(x2_5ET);
            entry.breakfast = (int) safeParse(breakfastET);
            entry.lunch = (int) safeParse(lunchET);
            entry.dinner = (int) safeParse(dinnerET);
            SettingsStore settings = new SettingsStore(this);
            entry.baseRate = settings.getRate();
            entry.breakfastRate = settings.getBreakfast();
            entry.lunchRate = settings.getLunch();
            entry.dinnerRate = settings.getDinner();
            entry.vacationHours = safeParse(vacationET);
            entry.restHours = safeParse(restET);

            store.saveLog(entry);
            Toast.makeText(this, "Work entry saved!", Toast.LENGTH_SHORT).show();

        });



        // </Store Work Log>

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

        // ========== MAIN ITEM ==========
        MenuItem mainItem = menu.findItem(R.id.menu_main);
        mainItem.setChecked(true);

        SpannableString mainText = new SpannableString(mainItem.getTitle());
        mainText.setSpan(new StyleSpan(Typeface.BOLD), 0, mainText.length(), 0);
        mainItem.setTitle(mainText);
    }

    // <Store work log method>

    private float safeParse(EditText et) {
        String text = et.getText().toString().trim();
        if (text.isEmpty()) return 0f;
        try {
            return Float.parseFloat(text);
        } catch (Exception e) {
            return 0f;
        }
    }
    // </Store work log method>


    // <Check if the date is in the right format method>
    private boolean isValidDate(String date) {
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) return false;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdf.setLenient(false);

        try {
            sdf.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    // </Check if the date is in the right format>

}
