package com.example.carolina.practicetimesix;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by carolina on 08/07/17.
 */

public class ActivityHelper {

    public void log (Context context, TextView textView, String message, boolean bolean){
        if(textView != null && message != null && !message.isEmpty()){
            try{
                Toast.makeText(context,message,Toast.LENGTH_LONG).show();

            } catch (Exception e){
                Log.e("Error", e.getMessage());
            }}

    }
}
