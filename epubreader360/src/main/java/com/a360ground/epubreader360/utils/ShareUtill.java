package com.a360ground.epubreader360.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Kiyos Solomon on 11/9/2016.
 */
public class ShareUtill {
    public static void share(String message,Context context){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }
}
