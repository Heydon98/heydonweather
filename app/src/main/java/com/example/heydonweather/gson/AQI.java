package com.example.heydonweather.gson;

/**
 * 空气质量实体类
 */
public class AQI {

    public AQICity city;

    public String status;

    public class AQICity {

        public String aqi;

        public String pm25;

    }

}
