package com.example.hourtracker;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.hourtracker.data.WorkLogIO;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ImportExportActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    private static final int REQUEST_IMPORT_JSON = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ie);

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

        // <Import and Export logic here>
        // JSON
        Button importButton = findViewById(R.id.ie_button_0);
        Button exportButton = findViewById(R.id.ie_button_1);
        importButton.setOnClickListener(v -> openFilePicker());
        exportButton.setOnClickListener(v -> exportLogs());

        // CSV
        Button csvButton = findViewById(R.id.ie_button_2);
        csvButton.setOnClickListener(v -> exportCSV());


        // </Import and Export logic here>

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

        // ========== IMPORT EXPORT ITEM ==========
        MenuItem ieItem = menu.findItem(R.id.menu_ie);
        ieItem.setChecked(true);

        SpannableString mainText = new SpannableString(ieItem.getTitle());
        mainText.setSpan(new StyleSpan(Typeface.BOLD), 0, mainText.length(), 0);
        ieItem.setTitle(mainText);
    }

    // Import and Export methods
    private void exportLogs() {
        try {
            WorkLogIO io = new WorkLogIO(this);
            JSONArray jsonArray = io.exportJson();
            String jsonString = jsonArray.toString(4);

            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd-HHmm").format(new java.util.Date());
            String fileName = "HourTracker_" + timestamp + ".json";

            File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloads, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(jsonString.getBytes(StandardCharsets.UTF_8));
            fos.close();

            Toast.makeText(this, "Exported to Downloads/" + fileName, Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Import Logic
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_IMPORT_JSON);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMPORT_JSON && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) importLogs(uri);
        }
    }

    private void importLogs(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            if (is == null) return;

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) builder.append(line);

            JSONArray jsonArray = new JSONArray(builder.toString());

            WorkLogIO io = new WorkLogIO(this);
            io.importJson(jsonArray);

            Toast.makeText(this, "Import successful!", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Toast.makeText(this, "Import failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // CSV Export method
    private void exportCSV() {
        try {
            WorkLogIO io = new WorkLogIO(this);
            JSONArray jsonArray = io.exportJson();

            // Create header for CSV
            StringBuilder csvData = new StringBuilder();
            csvData.append("Date,1.0X,1.5X,2.0X,2.5X,Breakfast,Lunch,Dinner,Vacation,Rest\n");

            // Convert JSON to CSV rows
            for (int i = 0; i <jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                csvData.append(obj.optString("date")).append(",");
                csvData.append(obj.optDouble("x1Hours", 0)).append(",");
                csvData.append(obj.optDouble("x1_5Hours", 0)).append(",");
                csvData.append(obj.optDouble("x2Hours", 0)).append(",");
                csvData.append(obj.optDouble("x2_5Hours", 0)).append(",");
                csvData.append(obj.optInt("breakfast", 0)).append(",");
                csvData.append(obj.optInt("lunch", 0)).append(",");
                csvData.append(obj.optInt("dinner", 0)).append(",");
                csvData.append(obj.optDouble("vacationHours", 0)).append(",");
                csvData.append(obj.optDouble("restHours", 0)).append("\n");
            }

            // Timestamp filename
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd-HHmm").format(new java.util.Date());
            String fileName = "HourTracker_" + timestamp + ".csv";

            // Save to Downloads
            File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloads, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(csvData.toString().getBytes(StandardCharsets.UTF_8));
            fos.close();

            Toast.makeText(this, "CSV exported to Downloads/" + fileName, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "CSV export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

}
