package com.example.heydonweather.gson;

import android.widget.LinearLayout;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 建议实体类
 */
public class Suggestion {

    public String status;

    @SerializedName("lifestyle")
    public List<LifeStyle> lifeStyleList;

    public class LifeStyle {

        public String type;

        public String txt;

    }

}
