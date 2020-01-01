package com.example.heydonweather.gson;

import android.widget.LinearLayout;

import com.google.gson.annotations.SerializedName;

/**
 * 建议实体类
 */
public class Suggestion {

    public String status;

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    public class Comfort {

        @SerializedName("txt")
        public String info;

    }

    public class CarWash {

        @SerializedName("txt")
        public String info;

    }

    public class Sport {

        @SerializedName("txt")
        public String info;

    }

}
