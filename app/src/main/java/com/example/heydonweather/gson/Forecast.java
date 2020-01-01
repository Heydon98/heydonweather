package com.example.heydonweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 未来预报实体类
 */
public class Forecast {

    public String status;

    @SerializedName("daily_forecast")
    public List<DailyForecast> dailyForecastList;

    public class DailyForecast {

        public String date;

        @SerializedName("tmp_max")
        public String max;

        @SerializedName("tmp_min")
        public String min;

        @SerializedName("cond_txt_d")
        public String info;
    }
}
