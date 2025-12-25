package com.example.hourtracker.data;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RangeCalculator {

    private final WorkLogStore workStore;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public RangeCalculator(Context context) {
        this.workStore = new WorkLogStore(context);
    }

    public static class Result {
        public float total1;
        public float total15;
        public float total2;
        public float total25;

        public int bf;
        public int lunch;
        public int dinner;

        public float vacation;
        public float rest;

        public float totalPay;
    }

    public Result calculate(String startStr, String endStr) {
        Result r = new Result();

        try {
            Date start = sdf.parse(startStr);
            Date end = sdf.parse(endStr);

            List<WorkLogStore.WorkEntry> logs = workStore.getAllLogs();

            for (WorkLogStore.WorkEntry e : logs) {
                Date d = sdf.parse(e.date);

                if (!d.before(start) && !d.after(end)) {

                    // accumulate totals
                    r.total1 += e.x1Hours;
                    r.total15 += e.x1_5Hours;
                    r.total2 += e.x2Hours;
                    r.total25 += e.x2_5Hours;

                    r.bf += e.breakfast;
                    r.lunch += e.lunch;
                    r.dinner += e.dinner;

                    r.vacation += e.vacationHours;
                    r.rest += e.restHours;

                    // Pay using the user's rates while inputting entry.
                    r.totalPay += e.x1Hours * e.baseRate;
                    r.totalPay += e.x1_5Hours * e.baseRate * 1.5f;
                    r.totalPay += e.x2Hours * e.baseRate * 2f;
                    r.totalPay += e.x2_5Hours * e.baseRate * 2.5f;

                    r.totalPay += e.breakfast * e.breakfastRate;
                    r.totalPay += e.lunch * e.lunchRate;
                    r.totalPay += e.dinner * e.dinnerRate;

                    r.totalPay += e.vacationHours * e.baseRate;
                    r.totalPay += e.restHours * e.baseRate;

                }
            }

        } catch (Exception e) {

        }

        return r;
    }
}
