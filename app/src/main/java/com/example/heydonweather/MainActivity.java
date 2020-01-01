package com.example.heydonweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.heydonweather.gson.AQI;
import com.example.heydonweather.gson.Forecast;
import com.example.heydonweather.gson.Now;
import com.example.heydonweather.gson.Suggestion;
import com.example.heydonweather.gson.Weather;
import com.example.heydonweather.util.Utility;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs;
        prefs = getBaseContext().getSharedPreferences("now", MODE_PRIVATE);
        String nowString = prefs.getString("now", null);
        prefs = getBaseContext().getSharedPreferences("forecast", MODE_PRIVATE);
        String forecastString = prefs.getString("forecast", null);
        prefs = getBaseContext().getSharedPreferences("suggestion", MODE_PRIVATE);
        String suggestionString = prefs.getString("suggestion", null);
        prefs = getBaseContext().getSharedPreferences("air", MODE_PRIVATE);
        String airString = prefs.getString("air", null);
        Log.e("MA", "" + nowString);
        Log.e("MA", "" + forecastString);
        Log.e("MA", "" + suggestionString);
        Log.e("MA", "" + airString);
        //判断是否存在天气缓存
        if (nowString != null && forecastString != null && suggestionString != null && airString != null) {
            Now nowSF = Utility.handleNowResponse(nowString);
            Suggestion suggestionSF = Utility.handleSuggestionResponse(suggestionString);
            Forecast forecastSF = Utility.handleForecastResponse(forecastString);
            AQI aqiSF = Utility.handleAQIResponse(airString);
            Weather weather = new Weather(nowSF, forecastSF, suggestionSF, aqiSF);
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();

        }

    }
}
