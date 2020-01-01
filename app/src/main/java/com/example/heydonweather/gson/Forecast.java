package com.example.heydonweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 未来预报实体类
 */
public class Forecast {

    public String status;

    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature {

        public String max;

        public String min;

    }

    public class More {

        @SerializedName("txt_d")
        public String info;

    }

}
