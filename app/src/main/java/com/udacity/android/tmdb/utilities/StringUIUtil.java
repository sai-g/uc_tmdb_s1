package com.udacity.android.tmdb.utilities;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.udacity.android.tmdb.R;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.squareup.picasso.Picasso.with;

/**
 * Created by sai_g on 2/5/17.
 */

public class StringUIUtil {

    private StringUIUtil() {}

    // formatter to show numbers as xx.x or x.x
    public final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0");

    private static SimpleDateFormat shortenedDateFormat = null;
    private static SimpleDateFormat inputDateFormat = null;


    public static String getFinalString(View view, int strResourceId, String... strings) {

        if (view == null)
            return null;
        return view.getResources().getString(strResourceId, strings);
    }

    public static void setStringResource(View view, int resourceId, String... strings) {

        if (view instanceof TextView) {
            ((TextView) view).setText(getFinalString(view, resourceId, strings));
            ((TextView) view).setVisibility(View.VISIBLE);
        }
    }

    public static String getFriendlyDateString(String dateStr) {

        if (dateStr != null) {
            try {
                Date date = getInputDateFormat().parse(dateStr);
                return getShortenedDateFormat().format(date);
            } catch (ParseException e) {
                return null;
            }
        }
        return null;
    }

    public static void setImageResource(Context context, ImageView imageView, URL imageUrl, int[] alternateImageOptions) {

        if(imageUrl != null) {
            // enabling picasso logs to check cache status, whether image loaded from network, disk or memory
            Picasso picasso = Picasso.with(context);
            picasso.setIndicatorsEnabled(true);
            //picasso.setLoggingEnabled(true);
                    picasso.load(imageUrl.toString())
                    .placeholder(R.drawable.placeholder)
                    .error(alternateImageOptions[0])
                    .into(imageView);
        }
        else {
            with(context).load(alternateImageOptions[0])
                    .resize(alternateImageOptions[1],alternateImageOptions[2])
                    .onlyScaleDown().into(imageView);
        }
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * To calculate number of columns based on device width
     * reference : http://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
     * @param context
     * @return
     */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }

    public static byte[] convertObjToBytes(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object).getBytes();
    }

    private static SimpleDateFormat getShortenedDateFormat() {
        if (shortenedDateFormat == null) {
            shortenedDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        }
        return shortenedDateFormat;
    }
    private static SimpleDateFormat getInputDateFormat() {
        if (inputDateFormat == null) {
            inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        }
        return inputDateFormat;
    }
}
