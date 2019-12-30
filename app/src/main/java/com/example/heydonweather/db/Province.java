package com.example.heydonweather.db;

import org.litepal.crud.DataSupport;

public class Province extends DataSupport {

    private int id;     //实体类ID

    private int provinceCode;   //省代码

    private String provinceName;    //省名称

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }


}
