package com.example.heydonweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 实况天气实体类
 */
public class Now {

    @SerializedName("temp")
    public String tempreature;

    @SerializedName("cond")
    public More more;

    public class More {

        @SerializedName("txt")
        public String info;

    }

}
