package com.example.heydonweather.db;

import org.litepal.crud.DataSupport;

public class City extends DataSupport {

    private int id;     //实体类ID

    private String cityName;       //城市名称

    private int cityNode;       //城市代码

    private int provinceId;     //当前市所属省ID

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityNode() {
        return cityNode;
    }

    public void setCityNode(int cityNode) {
        this.cityNode = cityNode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
