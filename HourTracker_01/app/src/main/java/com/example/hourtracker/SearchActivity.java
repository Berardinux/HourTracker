package com.example.hourtracker;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.hourtracker.data.WorkLogStore;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class SearchActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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

        // <Search results>
        EditText searchEt = findViewById(R.id.search_edittext_0);
        TextView resultsTv = findViewById(R.id.search_textview_0);
        WorkLogStore store = new WorkLogStore(this);

        // Listen live - searches as the user types
        searchEt.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                runSearch(s.toString().trim().toLowerCase());
            }
        });
        // </Search results>

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

        // ========== SEARCH ITEM ==========
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchItem.setChecked(true);

        SpannableString mainText = new SpannableString(searchItem.getTitle());
        mainText.setSpan(new StyleSpan(Typeface.BOLD), 0, mainText.length(), 0);
        searchItem.setTitle(mainText);
    }

    // Search method
    private void runSearch(String key) {
        TextView resultsTv = findViewById(R.id.search_textview_0);
        WorkLogStore store = new WorkLogStore(this);

        if (key.isEmpty()) {
            resultsTv.setText("");
            return;
        }

        StringBuilder out = new StringBuilder();

        for (WorkLogStore.WorkEntry e: store.getAllLogs()) {
            if ("vacation".contains(key) && e.vacationHours > 0)
                out.append(e.date).append(" ➜ Vacation: ").append(e.vacationHours).append("h\n");

            if ("rest".contains(key) && e.restHours > 0)
                out.append(e.date).append(" ➜ Rest: ").append(e.restHours).append("h\n");

            if ("dinner".contains(key) && e.dinner > 0)
                out.append(e.date).append(" ➜ Dinner: ").append(e.dinner).append("\n");

            if ("lunch".contains(key) && e.lunch > 0)
                out.append(e.date).append(" ➜ Lunch: ").append(e.lunch).append("\n");

            if ("breakfast".contains(key) && e.breakfast > 0)
                out.append(e.date).append(" ➜ Breakfast: ").append(e.breakfast).append("\n");

            if ("x1.5".contains(key) && e.x1_5Hours > 0)
                out.append(e.date).append(" ➜ x1.5 Hours: ").append(e.x1_5Hours).append("h\n");

            if ("x2.5".contains(key) && e.x2_5Hours > 0)
                out.append(e.date).append(" ➜ x2.5 Hours: ").append(e.x2_5Hours).append("h\n");

            if ("x2".contains(key) && e.x2Hours > 0)
                out.append(e.date).append(" ➜ x2 Hours: ").append(e.x2Hours).append("h\n");

            if ("x1".contains(key) && e.x1Hours > 0)
                out.append(e.date).append(" ➜ x1 Hours: ").append(e.x1Hours).append("h\n");
        }

        resultsTv.setText(out.length() == 0 ? "No entries found for \""+key+"\"" : out.toString());

    }

}
