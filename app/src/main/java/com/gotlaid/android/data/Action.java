package com.gotlaid.android.data;

import com.google.firebase.database.Exclude;
import com.gotlaid.android.MainActivity;
import com.gotlaid.android.R;

import org.ocpsoft.pretty.time.PrettyTime;

import java.util.Date;

/**
 * Created by zigapk on 6.7.2016.
 */

public class Action {
    public long timestamp;
    public String user_display_name;
    public String user_first_name;
    public String user_id;

    public Action(){}

    //sets timestamp to current time
    public Action(String user_display_name, String user_fisrt_name, String user_id){
        this.user_display_name = user_display_name;
        this.user_first_name = user_fisrt_name;
        this.user_id = user_id;
        timestamp = System.currentTimeMillis() / 1000L;
    }

    public Action(String user_display_name, String user_fisrt_name, String user_id, long timestamp){
        this.user_display_name = user_display_name;
        this.user_first_name = user_fisrt_name;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }

    @Exclude
    public String getDisplayString() {
        String result = MainActivity.context.getString(R.string.got_laid, user_first_name);
        Date date = new Date((long) timestamp*1000);
        String formated = new PrettyTime().format(date);
        return result + " " + formated;
    }
}