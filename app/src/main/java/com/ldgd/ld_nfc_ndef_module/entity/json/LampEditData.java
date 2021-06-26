package com.ldgd.ld_nfc_ndef_module.entity.json;

/**
 * Created by ldgd on 2021/6/26.
 *
 */

public class LampEditData {

    /**
     * LAT : 22.635641
     * LNG : 114.004099
     * NAME : 英飞特
     * Pole_height : 18
     */

    private String UUID;
    private String LAT;  // 经度
    private String LNG;  // 纬度
    private String NAME; // 灯杆名称
    private String LampDiameter; // 灯杆直径
    private String Power_Manufacturer; // 电源出厂商
    private String Lamp_RatedCurrent; // 灯具额定电流
    private String Lamp_Ratedvoltage; // 灯具额定电压
    private String lampType; // 灯具类型
    private String Lamp_Manufacturer; // 灯具出厂商
    private String Lamp_Num; // 灯具数
    private String PoleProductionDate; // 灯具出厂日期
    private String Pole_height; // 灯杆高度
    private String Rated_power; // 总额定功率
    private String Subcommunicate_mode; // 通讯方式



    public String getLAT() {
        return LAT;
    }

    public void setLAT(String LAT) {
        this.LAT = LAT;
    }

    public String getLNG() {
        return LNG;
    }

    public void setLNG(String LNG) {
        this.LNG = LNG;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getPole_height() {
        return Pole_height;
    }

    public void setPole_height(String Pole_height) {
        this.Pole_height = Pole_height;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getLampDiameter() {
        return LampDiameter;
    }

    public void setLampDiameter(String lampDiameter) {
        LampDiameter = lampDiameter;
    }

    public String getPower_Manufacturer() {
        return Power_Manufacturer;
    }

    public void setPower_Manufacturer(String power_Manufacturer) {
        Power_Manufacturer = power_Manufacturer;
    }

    public String getLamp_RatedCurrent() {
        return Lamp_RatedCurrent;
    }

    public void setLamp_RatedCurrent(String lamp_RatedCurrent) {
        Lamp_RatedCurrent = lamp_RatedCurrent;
    }

    public String getLamp_Ratedvoltage() {
        return Lamp_Ratedvoltage;
    }

    public void setLamp_Ratedvoltage(String lamp_Ratedvoltage) {
        Lamp_Ratedvoltage = lamp_Ratedvoltage;
    }

    public String getLampType() {
        return lampType;
    }

    public void setLampType(String lampType) {
        this.lampType = lampType;
    }

    public String getLamp_Manufacturer() {
        return Lamp_Manufacturer;
    }

    public void setLamp_Manufacturer(String lamp_Manufacturer) {
        Lamp_Manufacturer = lamp_Manufacturer;
    }

    public String getLamp_Num() {
        return Lamp_Num;
    }

    public void setLamp_Num(String lamp_Num) {
        Lamp_Num = lamp_Num;
    }

    public String getPoleProductionDate() {
        return PoleProductionDate;
    }

    public void setPoleProductionDate(String poleProductionDate) {
        PoleProductionDate = poleProductionDate;
    }

    public String getRated_power() {
        return Rated_power;
    }

    public void setRated_power(String rated_power) {
        Rated_power = rated_power;
    }

    public String getSubcommunicate_mode() {
        return Subcommunicate_mode;
    }

    public void setSubcommunicate_mode(String subcommunicate_mode) {
        Subcommunicate_mode = subcommunicate_mode;
    }
}
