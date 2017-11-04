package com.example.mohamadghalayini.curators;

/**
 * Created by Mohamad Ghalayini on 2017-11-01.
 */
        import android.content.Context;
        import android.content.SharedPreferences;

/**
 * Created by Mohamad Ghalayini on 2017-09-23.
 */

public class SharedPreferenceHelper {

    private SharedPreferences sharedPreferences;
    public SharedPreferenceHelper(Context context)
    {
        sharedPreferences = context.getSharedPreferences("ProfilePreference",
                Context.MODE_PRIVATE );
    }
    public void saveAdminStatus(String adminStatus)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("adminStatus",adminStatus );
        editor.commit();
    }
    public String getAdminStatus()
    {
        return sharedPreferences.getString("adminStatus", "basic");
    }

}
