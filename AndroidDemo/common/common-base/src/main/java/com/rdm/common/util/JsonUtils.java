package com.rdm.common.util;

import com.rdm.common.ILog;
import com.rdm.common.util.time.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by Administrator on 2015/2/3.
 */
public class JsonUtils {

    public static boolean getBoolean(JSONObject json,String name,boolean defaultValue) {

        if (json == null) {
            throw new RuntimeException();
        }
        try {
            return json.getBoolean(name);
        } catch (JSONException e) {

            String text = getString(json, name, defaultValue + "");
            try{
                return Boolean.parseBoolean(text);
            }catch (Exception ex){

            }

            return defaultValue;
        }
    }

    public static int getInteger(JSONObject json,String name,int defaultValue){

        if(json == null){
            throw new RuntimeException();
        }
        try {
            return json.getInt(name);
        } catch (JSONException e) {
            String text = getString(json, name, defaultValue + "");
            try{
                return Integer.parseInt(text);
            }catch (Exception ex){
            }
            return defaultValue;
        }
    }

    public static String getString(JSONObject json,String name,String defaultValue){
        if(json == null){
            throw new RuntimeException();
        }
        try {
            return json.getString(name);
        } catch (JSONException e) {
            return defaultValue;
        }
    }


    public static Date getDate(JSONObject json,String name,String pattern,Date defaultValue){
        if(json == null){
            throw new RuntimeException();
        }
        String dateValue = getString(json, name, null);
        if(dateValue == null){
            return null;
        }
        try {
            Date  date = DateUtils.parseDate(dateValue, pattern);
            return date;
        } catch (ParseException e) {
            ILog.w(JsonUtils.class,e.getMessage(),e);
            return defaultValue;
        }
    }
}
