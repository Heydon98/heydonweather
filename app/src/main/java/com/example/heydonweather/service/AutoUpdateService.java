package com.example.heydonweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.example.heydonweather.WeatherActivity;
import com.example.heydonweather.gson.AQI;
import com.example.heydonweather.gson.Forecast;
import com.example.heydonweather.gson.Now;
import com.example.heydonweather.gson.Suggestion;
import com.example.heydonweather.gson.Weather;
import com.example.heydonweather.util.HttpUtil;
import com.example.heydonweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    private SharedPreferences nowPrefs;

    private SharedPreferences forecastPrefs;

    private SharedPreferences suggestionPrefs;

    private SharedPreferences airPrefs;

    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 1 * 60 * 60 * 1000;        //一小时毫秒
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气
     */
    private void updateWeather() {
        nowPrefs = getApplicationContext().getSharedPreferences("now", MODE_PRIVATE);
        String nowString = nowPrefs.getString("now", null);
        forecastPrefs = getApplicationContext().getSharedPreferences("forecast", MODE_PRIVATE);
        suggestionPrefs = getApplicationContext().getSharedPreferences("suggestion", MODE_PRIVATE);
        airPrefs = getApplicationContext().getSharedPreferences("air", MODE_PRIVATE);
        if (nowString != null) {
            //有缓存直接更新数据
            Now now = Utility.handleNowResponse(nowString);
            String weatherId = now.basic.weatherId;

            requestNow(weatherId);
            requestForecast(weatherId);
            requestAQI(weatherId);
            requestSuggestion(weatherId);

        }
    }

    //请求实况天气
    public void requestNow(String weatherId) {
        String nowUrl = "https://free-api.heweather.net/s6/weather/now?location=" + weatherId + "&key=8bbf094769c8441d9de07395c273f230";
        HttpUtil.sendOkHttpRequest(nowUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Now nowResponse = Utility.handleNowResponse(responseText);
                if (nowResponse != null && "ok".equals(nowResponse.status)) {
                    SharedPreferences.Editor editor = nowPrefs.edit();
                    editor.putString("now", responseText).commit();
                }
            }
        });
    }

    public void requestForecast(String weatherId) {
        //请求预报数据
        String forecastUrl = "https://free-api.heweather.net/s6/weather/forecast?location=" + weatherId + "&key=8bbf094769c8441d9de07395c273f230";
        HttpUtil.sendOkHttpRequest(forecastUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Forecast forecastResponse = Utility.handleForecastResponse(responseText);
                if (forecastResponse != null && "ok".equals(forecastResponse.status)) {
                    SharedPreferences.Editor editor = forecastPrefs.edit();
                    editor.putString("forecast", responseText).commit();
                }
            }
        });
    }

    /**
     * 请求生活数据
     */
    public void requestSuggestion(String weatherId) {
        //请求预报数据
        String lifeStyleUrl = "https://free-api.heweather.net/s6/weather/lifestyle?location=" + weatherId + "&key=8bbf094769c8441d9de07395c273f230";
        HttpUtil.sendOkHttpRequest(lifeStyleUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Suggestion lifeStyleResponse = Utility.handleSuggestionResponse(responseText);
                if (lifeStyleResponse != null && "ok".equals(lifeStyleResponse.status)) {
                    SharedPreferences.Editor editor = suggestionPrefs.edit();
                    editor.putString("suggestion", responseText).commit();
                }
            }
        });
    }

    /**
     * 请求空气数据
     */
    public void requestAQI(String weatherId) {
        //请求预报数据
        String airUrl = "https://free-api.heweather.net/s6/air/now?location=" + weatherId + "&key=8bbf094769c8441d9de07395c273f230";
        HttpUtil.sendOkHttpRequest(airUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final AQI airResponse = Utility.handleAQIResponse(responseText);
                if (airResponse != null && "ok".equals(airResponse.status)) {
                    SharedPreferences.Editor editor = airPrefs.edit();
                    editor.putString("air", responseText).commit();
                }
            }
        });
    }
}
