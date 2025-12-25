package com.example.hourtracker.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsStore {
    private static final String PREF_NAME = "user_settings";
    private static final String KEY_RATE = "rate_per_hour";
    private static final String KEY_BREAKFAST = "pay_breakfast";
    private static final String KEY_LUNCH = "pay_lunch";
    private static final String KEY_DINNER = "pay_DINNER";

    private final SharedPreferences prefs;

    public SettingsStore(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE);
    }

    // Save rates methods
    public void saveRate(float rate) {
        prefs.edit().putFloat(KEY_RATE, rate).apply();
    }

    public void saveBreakfast(float amount) {
        prefs.edit().putFloat(KEY_BREAKFAST, amount).apply();
    }

    public void saveLunch(float amount) {
        prefs.edit().putFloat(KEY_LUNCH, amount).apply();
    }

    public void saveDinner(float amount) {
        prefs.edit().putFloat(KEY_DINNER, amount).apply();
    }

    // Loads rates methods
    public float getRate() {
        return prefs.getFloat(KEY_RATE, 0f);
    }

    public float getBreakfast() {
        return prefs.getFloat(KEY_BREAKFAST, 0f);
    }

    public float getLunch() {
        return prefs.getFloat(KEY_LUNCH, 0f);
    }

    public float getDinner() {
        return prefs.getFloat(KEY_DINNER, 0f);
    }
}
