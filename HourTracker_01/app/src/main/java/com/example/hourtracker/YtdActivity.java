package com.example.hourtracker;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.hourtracker.data.WorkLogStore;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class YtdActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ytd);

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

        // <Print YTD summary>
        TextView ytdTv = findViewById(R.id.ytd_textview_0);
        WorkLogStore store = new WorkLogStore(this);

        // Get the current year
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

        float total1 = 0, total15 = 0, total2 = 0, total25 = 0;
        int bf = 0, lunch = 0, dinner = 0;
        float vacation = 0, rest = 0;
        float totalPay = 0;

        for (WorkLogStore.WorkEntry e : store.getAllLogs()) {

            // Parse year from date format yyyy-MM-dd
            if (e.date == null || e.date.length() < 4) continue;
            int entryYear = Integer.parseInt(e.date.substring(0, 4));

            // Only count entries that match the current year
            if (entryYear != currentYear) continue;

            total1 += e.x1Hours;
            total15 += e.x1_5Hours;
            total2 += e.x2Hours;
            total25 += e.x2_5Hours;

            bf += e.breakfast;
            lunch += e.lunch;
            dinner += e.dinner;

            vacation += e.vacationHours;
            rest += e.restHours;

            totalPay += e.x1Hours * e.baseRate;
            totalPay += e.x1_5Hours * e.baseRate * 1.5f;
            totalPay += e.x2Hours * e.baseRate * 2f;
            totalPay += e.x2_5Hours * e.baseRate * 2.5f;

            totalPay += e.breakfast * e.breakfastRate;
            totalPay += e.lunch * e.lunchRate;
            totalPay += e.dinner * e.dinnerRate;

            totalPay += e.vacationHours * e.baseRate;
            totalPay += e.restHours * e.baseRate;
        }

        // Formate and display
        String summary =
                "<font color='#FFFFFF'>Total x1 Hours: </font>"
                        + "<font color='#FF9999'>" + total1 + "</font><br>"

                        + "<font color='#FFFFFF'>Total x1.5 Hours: </font>"
                        + "<font color='#FF9999'>" + total15 + "</font><br>"

                        + "<font color='#FFFFFF'>Total x2 Hours: </font>"
                        + "<font color='#FF9999'>" + total2 + "</font><br>"

                        + "<font color='#FFFFFF'>Total x2.5 Hours: </font>"
                        + "<font color='#FF9999'>" + total25 + "</font><br><br>"

                        + "<font color='#FFFFFF'>Breakfasts: </font>"
                        + "<font color='#66FF66'>" + bf + "</font><br>"

                        + "<font color='#FFFFFF'>Lunches: </font>"
                        + "<font color='#66FF66'>" + lunch + "</font><br>"

                        + "<font color='#FFFFFF'>Dinners: </font>"
                        + "<font color='#66FF66'>" + dinner + "</font><br><br>"

                        + "<font color='#FFFFFF'>Vacation Hours: </font>"
                        + "<font color='#9999FF'>" + vacation + "</font><br>"

                        + "<font color='#FFFFFF'>Rest Hours: </font>"
                        + "<font color='#9999FF'>" + rest + "</font><br><br>"

                        + "<font color='#FFFFFF'>________________________________</font><br><br>"
                        + "<font color='#FFFFFF'><b>YTD TOTAL PAY: </font>"
                        + "<font color='#FF7777'>$"
                        + String.format(Locale.getDefault(), "%.2f", totalPay)
                        + "</b></font>";
        ytdTv.setText(android.text.Html.fromHtml(summary));
        // </Print YTD summary>

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

        // ========== YTD ITEM ==========
        MenuItem ytdItem = menu.findItem(R.id.menu_ytd);
        ytdItem.setChecked(true);

        SpannableString mainText = new SpannableString(ytdItem.getTitle());
        mainText.setSpan(new StyleSpan(Typeface.BOLD), 0, mainText.length(), 0);
        ytdItem.setTitle(mainText);
    }

}
