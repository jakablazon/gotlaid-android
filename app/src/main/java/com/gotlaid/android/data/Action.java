package com.gotlaid.android.data;

/**
 * Created by zigapk on 6.7.2016.
 */

public class Action {
    public long timestamp;
    public String user_display_name;
    public String user_fisrt_name;
    public String user_id;

    public Action(){}

    //sets timestamp to current time
    public Action(String user_display_name, String user_fisrt_name, String user_id){
        this.user_display_name = user_display_name;
        this.user_fisrt_name = user_fisrt_name;
        this.user_id = user_id;
        timestamp = System.currentTimeMillis() / 1000L;
    }

    public Action(String user_display_name, String user_fisrt_name, String user_id, long timestamp){
        this.user_display_name = user_display_name;
        this.user_fisrt_name = user_fisrt_name;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }
}