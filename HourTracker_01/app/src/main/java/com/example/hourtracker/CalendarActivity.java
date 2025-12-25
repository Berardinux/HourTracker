package com.example.hourtracker;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.hourtracker.data.RangeCalculator;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // </Navigation Drawer setup>
        // Set hamburger icon behavior
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        // spacer for camera.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbar), (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(0, topInset, 0, 0);
            return insets;
        });
        updateNavdrawer();
        // </Navigation Drawer setup>

        // <Setup to display paystub>

        // Date formate
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        // Calendar views
        CalendarView startCal = findViewById(R.id.calendar_start);
        CalendarView endCal = findViewById(R.id.calendar_end);
        // Store selected dates
        final String[] startDate = { sdf.format(new Date(startCal.getDate()))};
        final String[] endDate = { sdf.format(new Date(endCal.getDate()))};
        // Update selected dates
        startCal.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            month += 1;
            startDate[0] = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, dayOfMonth);
        });

        endCal.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            month += 1;
            endDate[0] = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, dayOfMonth);
        });
        // Calculate Button
        Button calcBtn = findViewById(R.id.calendar_button_0);
        TextView resultTv = findViewById(R.id.calendar_textview_0);

        // </Setup to display paystub>

        // <Display paystub>

        calcBtn.setOnClickListener(v -> {
            try {
                Date start = sdf.parse(startDate[0]);
                Date end = sdf.parse(endDate[0]);

                if (start == null || end == null) {
                    Toast.makeText(
                            this,
                            "❌ Error reading dates.",
                            Toast.LENGTH_LONG
                    ).show();
                    return;
                }

                if (start.after(end)) {
                    Toast.makeText(
                            this,
                            "❌ Error: End date cannot be before start date.",
                            Toast.LENGTH_LONG
                    ).show();
                    return;
                }

                RangeCalculator calc = new RangeCalculator(this);
                RangeCalculator.Result r = calc.calculate(startDate[0], endDate[0]);

                // If nothing found
                if (
                        r.total1 == 0 &&
                        r.total15 == 0 &&
                        r.total2 == 0 &&
                        r.total25 == 0 &&
                        r.bf == 0 &&
                        r.lunch == 0 &&
                        r.dinner == 0 &&
                        r.vacation == 0 &&
                        r.rest == 0
                ) {

                    resultTv.setText("No work entries found in this date range.");
                    return;
                }

                String summary =
                        "<font color='#FFFFFF'><b>Date Range {<br>&nbsp; &nbsp; &nbsp; &nbsp;</b> "
                        + "<font color='#66CCFF'>" + startDate[0] + "</font>"
                        + "  ➜  "
                        + "<font color='#66CCFF'>" + endDate[0] + "</font><br>}<br><br></font>"

                        +"<font color='#FFFFFF'>Total x1 Hours: </font>"
                        + "<font color='#FF9999'>" + r.total1 + "</font><br>"

                        + "<font color='#FFFFFF'>Total x1.5 Hours: </font>"
                        + "<font color='#FF9999'>" + r.total15 + "</font><br>"

                        + "<font color='#FFFFFF'>Total x2 Hours: </font>"
                        + "<font color='#FF9999'>" + r.total2 + "</font><br>"

                        + "<font color='#FFFFFF'>Total x2.5 Hours: </font>"
                        + "<font color='#FF9999'>" + r.total25 + "</font><br><br>"

                        + "<font color='#FFFFFF'>Breakfasts: </font>"
                        + "<font color='#66FF66'>" + r.bf + "</font><br>"

                        + "<font color='#FFFFFF'>Lunches: </font>"
                        + "<font color='#66FF66'>" + r.lunch + "</font><br>"

                        + "<font color='#FFFFFF'>Dinners: </font>"
                        + "<font color='#66FF66'>" + r.dinner + "</font><br><br>"

                        + "<font color='#FFFFFF'>Vacation Hours: </font>"
                        + "<font color='#9999FF'>" + r.vacation + "</font><br>"

                        + "<font color='#FFFFFF'>Rest Hours: </font>"
                        + "<font color='#9999FF'>" + r.rest + "</font><br><br>"

                        + "<font color='#FFFFFF'>________________________________</font><br><br>"
                        + "<font color='#FFFFFF'><b>TOTAL PAY: </font>"
                        + "<font color='#FF7777'>$"
                        + String.format(Locale.getDefault(), "%.2f", r.totalPay)
                        + "</b></font>";

                resultTv.setText(Html.fromHtml(summary));


            } catch (Exception e) {
                Toast.makeText(
                        this,
                        "❌ Unexpected error calculating pay.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        // </Display paystub>

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

    public void updateNavdrawer() {
        Menu menu = navigationView.getMenu();

        // ========== CALENDAR ITEM ==========
        MenuItem calendarItem = menu.findItem(R.id.menu_calendar);
        calendarItem.setChecked(true);

        SpannableString zeroText = new SpannableString(calendarItem.getTitle());
        zeroText.setSpan(new StyleSpan(Typeface.BOLD), 0, zeroText.length(), 0);
        calendarItem.setTitle(zeroText);

    }
}

