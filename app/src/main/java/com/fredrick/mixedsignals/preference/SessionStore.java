package com.fredrick.mixedsignals.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionStore {
    
    private static final String KEY = "Mixed Signals";
    
    public static boolean save(Context context, String key, String value) {
        Editor editor =
            context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        
        return editor.commit();
    }
    
    public static boolean save(Context context, String key, int value) {
        Editor editor =
            context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
    
        return editor.commit();
    }
    
    public static boolean save(Context context, String key, long value) {
        Editor editor =
            context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putLong(key, value);
    
        return editor.commit();
    }
    
    public static boolean save(Context context, String key, boolean value) {
        Editor editor =
            context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        
        return editor.commit();
    }



    public static String restoreString(Context context, String key) {
    	
        SharedPreferences savedSession =
            context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        
        return savedSession.getString(key, "");
        
    }
    
    public static int restoreInt(Context context, String key) {
    	
        SharedPreferences savedSession =
            context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        
        int defaultValue = -1;
        
        return savedSession.getInt(key, defaultValue);
        
    }
    
    public static long restoreLong(Context context, String key) {
    	
        SharedPreferences savedSession =
            context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        
        long defaultValue = -1;
        
        return savedSession.getLong(key, defaultValue);
        
    }
    
    public static boolean restoreBoolean(Context context, String key) {
    	
        SharedPreferences savedSession =
            context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        
        boolean defaultValue = false;
        
        return savedSession.getBoolean(key, defaultValue);
        
    }

    public static void clear(Context context) {
        Editor editor = 
            context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }
}
