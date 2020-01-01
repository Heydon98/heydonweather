package com.example.heydonweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 实例类
 */
public class Weather {

    public Now now;

    public Forecast forecast;

    public Suggestion suggestion;

    public AQI aqi;

    /**
     * 构造器
     */
    public Weather(Now now, Forecast forecast, Suggestion suggestion, AQI aqi) {

        this.now = now;

        this.forecast = forecast;

        this.suggestion = suggestion;

        this.aqi = aqi;

    }

}
