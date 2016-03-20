package jp.cordea.mapboxdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.net.ContentHandler;

/**
 * Created by CORDEA on 2016/03/20.
 */
public class PreferenceUtils {

    private static final String TOKEN_KEY = "Tokenkey";

    public static String getTokenKey(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(TOKEN_KEY, null);
    }

    public static void putTokenKey(Context context, String token) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(TOKEN_KEY, token).apply();
    }
}
