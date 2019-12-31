package com.example.heydonweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.heydonweather.gson.Weather;
import com.example.heydonweather.util.Utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = getBaseContext().getSharedPreferences("weather", MODE_PRIVATE);
        //判断是否存在天气缓存
        if (prefs.getString("weather", null) != null) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date now = new Date(System.currentTimeMillis());
            Date updateTime = new Date();
            Log.e("MA", "现在"  + df.format(now) + "缓存" + Utility.handleWeatherResponse(prefs.getString("weather", null)).basic.update.updateTime);
            try {
                now = df.parse(df.format(now));
                updateTime = df.parse(Utility.handleWeatherResponse(prefs.getString("weather", null)).basic.update.updateTime);
                Log.e("MA", "转换成功");
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("MA", "转换失败");
            }
            long diff = now.getTime() - updateTime.getTime();
            double result = diff * 1.0 / (1000 * 60);
            Log.e("MA", "差值" + result);
            //判断天气是否超过一小时
            if (result < 60) {
                Intent intent = new Intent(this, WeatherActivity.class);
                startActivity(intent);
                Log.e("main", "weatherA start");
                finish();
            }
        }

    }
}
