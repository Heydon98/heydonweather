package com.example.heydonweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.heydonweather.gson.AQI;
import com.example.heydonweather.gson.Forecast;
import com.example.heydonweather.gson.Now;
import com.example.heydonweather.gson.Suggestion;
import com.example.heydonweather.gson.Weather;
import com.example.heydonweather.service.AutoUpdateService;
import com.example.heydonweather.util.HttpUtil;
import com.example.heydonweather.util.Utility;


import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private SharedPreferences nowPrefs;

    private SharedPreferences forecastPrefs;

    private SharedPreferences suggestionPrefs;

    private SharedPreferences airPrefs;

    public SwipeRefreshLayout swipeRefresh;

    //记录天气ID
    private String mWeatherId;

    private Now now;

    private Forecast forecast;

    private AQI aqi;

    private Suggestion suggestion;

    private ImageView picImg;

    /**
     * 控制线程同步
     */
    final CyclicBarrier barrier = new CyclicBarrier(9);

    public DrawerLayout drawerLayout;

    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //初始化各条件
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);
        picImg = (ImageView) findViewById(R.id.pic_img);

        //获取缓存
        nowPrefs = getApplicationContext().getSharedPreferences("now", MODE_PRIVATE);
        String nowString = nowPrefs.getString("now", null);
        forecastPrefs = getApplicationContext().getSharedPreferences("forecast", MODE_PRIVATE);
        String forecastString = forecastPrefs.getString("forecast", null);
        suggestionPrefs = getApplicationContext().getSharedPreferences("suggestion", MODE_PRIVATE);
        String suggestionString = suggestionPrefs.getString("suggestion", null);
        airPrefs = getApplicationContext().getSharedPreferences("air", MODE_PRIVATE);
        String airString = airPrefs.getString("air", null);
        if (nowString != null && forecastString != null && suggestionString != null && airString != null) {
            //有缓存直接显示
            Now nowSF = Utility.handleNowResponse(nowString);
            Suggestion suggestionSF = Utility.handleSuggestionResponse(suggestionString);
            Forecast forecastSF = Utility.handleForecastResponse(forecastString);
            AQI aqiSF = Utility.handleAQIResponse(airString);
            Weather weather = new Weather(nowSF,forecastSF, suggestionSF, aqiSF);
            mWeatherId = nowSF.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            //无缓存直接去服务器查询
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            try {
                requestWeather(weatherId);
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //下拉刷新监听
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    requestWeather(mWeatherId);
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        //切换城市按钮监听
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * 根据天气id请求城市天气信息
     */
    public void requestWeather(final String weatherId) throws BrokenBarrierException, InterruptedException {

        Log.e("WA", "weatherId=" + weatherId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                requestNow(weatherId);
//                latch.countDown();
                try {
                    Log.e("WA", "now" + barrier.getNumberWaiting());
                    barrier.await();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                requestForecast(weatherId);
//                latch.countDown();
                try {
                    Log.e("WA", "fore" + barrier.getNumberWaiting());
                    barrier.await();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                requestAQI(weatherId);
//                latch.countDown();
                try {
                    Log.e("WA", "aqi" + barrier.getNumberWaiting());
                    barrier.await();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                requestSuggestion(weatherId);
//                latch.countDown();
                try {
                    Log.e("WA", "sugg" + barrier.getNumberWaiting());
                    barrier.await();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Log.e("WA", "we" + barrier.getNumberWaiting());
        barrier.await();
        Log.e("WA", "now" + now.status + now.update.updateTime);
        Log.e("WA", "forecast" + forecast.status + now.update.updateTime);
        Log.e("WA", "AQI" + aqi.status + now.update.updateTime);
        Log.e("WA", "suggestion" + suggestion.status + now.update.updateTime);
        Weather weather = new Weather(now, forecast, suggestion, aqi);
        barrier.reset();
        swipeRefresh.setRefreshing(false);
        showWeatherInfo(weather);

    }

    //请求实况天气
    public void requestNow(String weatherId) {
        String nowUrl = "https://free-api.heweather.net/s6/weather/now?location=" + weatherId + "&key=8bbf094769c8441d9de07395c273f230";
        HttpUtil.sendOkHttpRequest(nowUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                try {
                    barrier.await();
                } catch (BrokenBarrierException ex) {
                    ex.printStackTrace();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.e("WA", "实况数据返回值：" + responseText);
                final Now nowResponse = Utility.handleNowResponse(responseText);
                if (nowResponse != null && "ok".equals(nowResponse.status)) {
                    now = nowResponse;
                    SharedPreferences.Editor editor = nowPrefs.edit();
                    editor.putString("now", responseText).commit();
                    mWeatherId = now.basic.weatherId;
                }
                try {
                    Log.e("WA", "nowR" + barrier.getNumberWaiting());
                    barrier.await();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
                try {
                    barrier.await();
                } catch (BrokenBarrierException ex) {
                    ex.printStackTrace();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.e("WA", "预报数据返回值：" + responseText);
                final Forecast forecastResponse = Utility.handleForecastResponse(responseText);
                if (forecastResponse != null && "ok".equals(forecastResponse.status)) {
                    forecast = forecastResponse;
                    SharedPreferences.Editor editor = forecastPrefs.edit();
                    editor.putString("forecast", responseText).commit();
                }
                try {
                    Log.e("WA", "foR" + barrier.getNumberWaiting());
                    barrier.await();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
                try {
                    barrier.await();
                } catch (BrokenBarrierException ex) {
                    ex.printStackTrace();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.e("WA", "生活数据返回值：" + responseText);
                final Suggestion lifeStyleResponse = Utility.handleSuggestionResponse(responseText);
                if (lifeStyleResponse != null && "ok".equals(lifeStyleResponse.status)) {
                    suggestion = lifeStyleResponse;
                    SharedPreferences.Editor editor = suggestionPrefs.edit();
                    editor.putString("suggestion", responseText).commit();
                }
                try {
                    Log.e("WA", "suR" + barrier.getNumberWaiting());
                    barrier.await();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
                try {
                    Log.e("WA", "" + barrier.getNumberWaiting());
                    barrier.await();
                } catch (BrokenBarrierException ex) {
                    ex.printStackTrace();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.e("WA", "空气数据返回值：" + responseText);
                final AQI airResponse = Utility.handleAQIResponse(responseText);
                if (airResponse != null && "ok".equals(airResponse.status)) {
                    aqi = airResponse;
                    SharedPreferences.Editor editor = airPrefs.edit();
                    editor.putString("air", responseText).commit();
                }
                try {
                    Log.e("WA", "aR" + barrier.getNumberWaiting());
                    barrier.await();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }            }
        });
    }

    /**
     * 处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather) {

        String cityName = weather.now.basic.cityName;
        String updateTime = weather.now.update.updateTime.split(" ")[1];
        String degree = weather.now.nowWeather.tempreature + "℃";
        String weatherInfo = weather.now.nowWeather.info;
        String comfort;
        String carWash;
        String sport;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        //加载预报试图
        for (Forecast.DailyForecast forecast : weather.forecast.dailyForecastList) {
//            Log.e("WA", "forecast" + forecast.date);
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dataText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dataText.setText(forecast.date);
            infoText.setText(forecast.info);
            maxText.setText(forecast.max);
            minText.setText(forecast.min);
            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.airInfo.aqi);
            pm25Text.setText(weather.aqi.airInfo.pm25);
        }
        for (Suggestion.LifeStyle lifeStyle : weather.suggestion.lifeStyleList) {

            if (lifeStyle.type.equals("comf")) {
                comfort = "舒适度：" + lifeStyle.txt;
                comfortText.setText(comfort);
            }
            if (lifeStyle.type.equals("cw")) {
                carWash = "洗车指数：" + lifeStyle.txt;
                carWashText.setText(carWash);
            }
            if (lifeStyle.type.equals("sport")) {
                sport = "活动建议：" + lifeStyle.txt;
                sportText.setText(sport);
            }
        }
        weatherLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }
}
