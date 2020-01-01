package com.example.heydonweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 实况天气实体类
 */
public class Now {

    public Basic basic;

    public class Basic {

        @SerializedName("location")
        public String cityName;

        @SerializedName("cid")
        public String weatherId;


    }

    public String status;

    public Update update;

    public class Update {

        @SerializedName("loc")
        public String updateTime;

    }

    @SerializedName("now")
    public NowWeather nowWeather;

    public class NowWeather {

        @SerializedName("tmp")
        public String tempreature;

        @SerializedName("cond_txt")
        public String info;

    }

}
