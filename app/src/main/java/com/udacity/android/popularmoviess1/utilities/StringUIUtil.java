package com.udacity.android.popularmoviess1.utilities;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sai_g on 2/5/17.
 */

public class StringUIUtil {

    private static SimpleDateFormat SHORTENED_DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy");
    private static SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public final static String IMAGE_WIDTH = "w342";

    public static String getFinalString(View view, int strResourceId, String... strings) {

        if (view == null)
            return null;
        return view.getResources().getString(strResourceId, strings);
    }

    public static void setStringResource(View view, int resourceId, String... strings) {

        if (view instanceof TextView) {
            ((TextView) view).setText(getFinalString(view, resourceId, strings));
        }
    }

    public static String getFriendlyDateString(String dateStr) {

        if (dateStr != null) {
            try {
                Date date = INPUT_DATE_FORMAT.parse(dateStr);
                return SHORTENED_DATE_FORMAT.format(date);
            } catch (ParseException e) {
                return null;
            }
        }
        return null;
    }

    public static void setImageResource(Context context, ImageView imageView, URL imageUrl, int altResourceId) {

        if(imageUrl != null) {
            // TODO add placeholder during image load
            Picasso.with(context).load(imageUrl.toString()).into(imageView);
        }
        else {
            Log.e("<IMAGE NOT FOUND>", context.toString());
            Picasso.with(context).load(altResourceId)
                    .resize(300,450).into(imageView);
        }
    }
}
