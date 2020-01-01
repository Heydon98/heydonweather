package com.example.heydonweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 实况天气实体类
 */
public class Now {

    public String status;

    @SerializedName("tmp")
    public String tempreature;

    @SerializedName("cond")
    public More more;

    public class More {

        @SerializedName("txt")
        public String info;

    }

}
