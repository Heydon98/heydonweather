package com.example.heydonweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heydonweather.gson.AQI;
import com.example.heydonweather.gson.Forecast;
import com.example.heydonweather.gson.LifeStyle;
import com.example.heydonweather.gson.Now;
import com.example.heydonweather.gson.Suggestion;
import com.example.heydonweather.gson.Weather;
import com.example.heydonweather.util.HttpUtil;
import com.example.heydonweather.util.Utility;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
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

    private SharedPreferences prefs;

    public SwipeRefreshLayout swipeRefresh;

    //记录天气ID
    private String mWeatherId;

    private Now now;

    private Forecast forecast;

    private AQI aqi;

    private Suggestion suggestion;

    private LifeStyle lifeStyle;

    final CountDownLatch latch = new CountDownLatch(4);

//    final CyclicBarrier cyclicBarrier = CyclicBarrier(4);

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
        prefs = getApplicationContext().getSharedPreferences("weather", MODE_PRIVATE);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        String weatherString = prefs.getString("weather", null);
//        String weatherId = getIntent().getStringExtra("weather_id");
//        weatherLayout.setVisibility(View.INVISIBLE);
//        requestWeather(weatherId);
        Log.e("WA", "缓存" + weatherString);
        if (weatherString != null) {
            //有缓存且缓存数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        } else {
            //无缓存直接去服务器查询
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
    }

    /**
     * 根据天气id请求城市天气信息
     */
    public void requestWeather(final String weatherId) {

        Log.e("WA", "weatherId=" + weatherId);
        requestNow(weatherId);
        requestForecast(weatherId);
        requestAQI(weatherId);
        requestSuggestion(weatherId);
        try {
            Log.e("WA", "1");
            latch.await();
        } catch (InterruptedException e) {
            Log.e("WA", "2");
            e.printStackTrace();
        }
        Log.e("WA", "now" + now);
        Log.e("WA", "forecast" + forecast);
        Log.e("WA", "AQI" + aqi);
        Log.e("WA", "suggestion" + suggestion);

//        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=8bbf094769c8441d9de07395c273f230";
//        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//                e.printStackTrace();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.e("WA", "fail");
//                        Toast.makeText(WeatherActivity.this, "获取天气数据失败", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//                final String responseText = response.body().string();
//                Log.e("WA", "天气数据返回值：" + responseText);
//                final Weather weather = Utility.handleWeatherResponse(responseText);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.e("WA", "status:" + weather.status);
//                        if (weather != null && "ok".equals(weather.status)) {
//                            SharedPreferences.Editor editor = prefs.edit();
//                            editor.putString("weather", responseText).commit();
//                            mWeatherId = weather.basic.weatherId;
//                            showWeatherInfo(weather);
//                        } else {
//                            Toast.makeText(WeatherActivity.this, "获取天气数据失败,解析失败", Toast.LENGTH_SHORT).show();
//                        }
//                        swipeRefresh.setRefreshing(false);
//                    }
//                });
//            }
//
//        });
    }

    //请求实况天气
    public void requestNow(String weatherId) {
        String nowUrl = "https://free-api.heweather.net/s6/weather/now?location=" + weatherId + "&key=8bbf094769c8441d9de07395c273f230";
        HttpUtil.sendOkHttpRequest(nowUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("WA", "获取实况数据失败");
//                        Toast.makeText(WeatherActivity.this, "获取实况数据失败", Toast.LENGTH_SHORT).show();
                        latch.countDown();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.e("WA", "实况数据返回值：" + responseText);
                final Now nowResponse = Utility.handleNowResponse(responseText);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (nowResponse != null && "ok".equals(nowResponse.status)) {
                            now = nowResponse;
                        } else {
//                            Toast.makeText(WeatherActivity.this, "获取实况失败,解析失败", Toast.LENGTH_SHORT).show();
                        }
                        latch.countDown();
                        Log.e("WA", "完成");
                    }
                }).start();
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("WA", "获取预报数据失败");
//                        Toast.makeText(WeatherActivity.this, "获取预报数据失败", Toast.LENGTH_SHORT).show();
                        latch.countDown();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.e("WA", "预报数据返回值：" + responseText);
                final Forecast forecastResponse = Utility.handleForecastResponse(responseText);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (forecastResponse != null && "ok".equals(forecastResponse.status)) {
                            forecast = forecastResponse;
                        } else {
//                            Toast.makeText(WeatherActivity.this, "获取预报数据失败,解析失败", Toast.LENGTH_SHORT).show();
                        }
                        latch.countDown();
                        Log.e("WA", "完成");
                    }
                }).start();
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("WA", "获取生活数据失败");
//                        Toast.makeText(WeatherActivity.this, "获取生活数据失败", Toast.LENGTH_SHORT).show();
                        latch.countDown();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.e("WA", "生活数据返回值：" + responseText);
                final LifeStyle lifeStyleResponse = Utility.handleLifeStyleResponse(responseText);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (lifeStyleResponse != null && "ok".equals(lifeStyleResponse.status)) {
                            lifeStyle = lifeStyleResponse;
                        } else {
//                            Toast.makeText(WeatherActivity.this, "获取生活数据失败,解析失败", Toast.LENGTH_SHORT).show();
                        }
                        latch.countDown();
                        Log.e("WA", "完成");
                    }
                }).start();
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("WA", "获取空气数据失败");
//                        Toast.makeText(WeatherActivity.this, "获取空气数据失败", Toast.LENGTH_SHORT).show();
                        latch.countDown();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.e("WA", "空气数据返回值：" + responseText);
                final AQI airResponse = Utility.handleAQIResponse(responseText);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (airResponse != null && "ok".equals(airResponse.status)) {
                            aqi = airResponse;
                        } else {
//                            Toast.makeText(WeatherActivity.this, "获取空气数据失败,解析失败", Toast.LENGTH_SHORT).show();
                        }
                        Log.e("WA", "完成");
                        latch.countDown();
                    }
                }).start();
            }
        });
    }

    /**
     * 处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather) {

        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.tempreature + "℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        //加载预报试图
        for (Forecast forecast : weather.forecastList) {
//            Log.e("WA", "forecast" + forecast.date);
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dataText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dataText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "活动建议：" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);

    }
}
