package com.ldgd.ld_nfc_module.entity;

/**
 * Created by ldgd on 2019/9/27.
 * 功能：
 * 说明：数据字典类，保存获取数据的方式
 */

public class DataDictionaries {

    private String name;
    private int startAddress;
    private int endAddress;
    private int takeByte; // 占用字节
    private String format; // 格式
    private String units;  // 单位
    private int factor;  // 系数
    private String operator;  // 运算符
    private String permission;  // 权限
    private String convertFormat; // 转换格式




    public String getConvertFormat() {
        return convertFormat;
    }
    public void setConvertFormat(String convertFormat) {
        this.convertFormat = convertFormat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(int startAddress) {
        this.startAddress = startAddress;
    }

    public int getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(int endAddress) {
        this.endAddress = endAddress;
    }

    public int getTakeByte() {
        return takeByte;
    }

    public void setTakeByte(int takeByte) {
        this.takeByte = takeByte;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public int getFactor() {
        return factor;
    }

    public void setFactor(int factor) {
        this.factor = factor;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
