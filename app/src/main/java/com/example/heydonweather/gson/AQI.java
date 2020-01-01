package com.example.heydonweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 空气质量实体类
 */
public class AQI {

    public String status;

    @SerializedName("air_now_city")
    public AirInfo airInfo;

    public class AirInfo {

        public String aqi;

        public String pm25;
    }

}
