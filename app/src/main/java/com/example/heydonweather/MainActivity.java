package com.example.heydonweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = getBaseContext().getSharedPreferences("weather", MODE_PRIVATE);
        //判断是否存在天气缓存
        if (prefs.getString("weather", null) != null) {

            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            Log.e("main", "weatherA start");
            finish();

        }

    }
}
