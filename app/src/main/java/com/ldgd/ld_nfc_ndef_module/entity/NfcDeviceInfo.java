package com.ldgd.ld_nfc_ndef_module.entity;

/**
 * Created by ldgd on 2019/10/12.
 * 功能：
 * 说明：nfc 设备信息
 */

public class NfcDeviceInfo {

    // 设备类型
    private String deviceType;
    // 更新标志位
    private String updateIndex;
    private String crc;
    // 主灯六段调光（时）
    private String mainLight1Hour;
    private String mainLight2Hour;
    private String mainLight3Hour;
    private String mainLight4Hour;
    private String mainLight5Hour;
    private String mainLight6Hour;
    // 主灯六段调光（分）
    private String mainLight1Minute;
    private String mainLight2Minute;
    private String mainLight3Minute;
    private String mainLight4Minute;
    private String mainLight5Minute;
    private String mainLight6Minute;
    // 主灯六段调光（亮度）
    private String mainLight1Brightness;
    private String mainLight2Brightness;
    private String mainLight3Brightness;
    private String mainLight4Brightness;
    private String mainLight5Brightness;
    private String mainLight6Brightness;
    // 辅灯六段调光（时）
    private String auxiliaryLight1Hour;
    private String auxiliaryLight2Hour;
    private String auxiliaryLight3Hour;
    private String auxiliaryLight4Hour;
    private String auxiliaryLight5Hour;
    private String auxiliaryLight6Hour;
    // 辅灯六段调光（分）
    private String auxiliaryLight1Minute;
    private String auxiliaryLight2Minute;
    private String auxiliaryLight3Minute;
    private String auxiliaryLight4Minute;
    private String auxiliaryLight5Minute;
    private String auxiliaryLight6Minute;
    // 辅灯六段调光（亮度）
    private String auxiliaryLight1Brightness;
    private String auxiliaryLight2Brightness;
    private String auxiliaryLight3Brightness;
    private String auxiliaryLight4Brightness;
    private String auxiliaryLight5Brightness;
    private String auxiliaryLight6Brightness;
    // 过流保护开关
    private String overcurrentProtectionWwitch;
    // 漏电保护开关
    private String earthLeakageCircuitBreaker;
    // 照度开灯开关
    private String illuminationLightSwitch;
    // 过压保护阈值
    private String overvoltageProtectionThreshold;
    // 欠压保护阈值
    private String undervoltageProtectionThreshold;
    // 过流保护阈值
    private String overcurrentProtectionThreshold;
    // 欠流保护阈值
    private String undercurrentProtectionThreshold;
    // 报警开关
    private String alarmSwitch;
    // 漏电保护阈值
    private String leakageProtectionThreshold;
    // 照度开灯阈值
    private String illuminationLightThreshold;
    // 照度关灯阈值
    private String lightOffThreshold;
    // 灯杆倒塌报警开关
    private String LampPoleCollapseAlarmSwitch;
    // 项目地区
    private String projectArea;
    // 项目编号
    private String projectNumber;
    // IMEI
    private String Imei;
    // 维修IMEI
    private String maintainImei;
    // 执行底板ID
    private String baseplateId;

    // NC
    private String nc;
    // 角度校准标志
    private String angleCalibrationSign;
    //   校准角度
    private String angleStandards;
    //  角度报警阈值误差
    private String angleAlarmThresholdArror;
    //  过压报警标志
    private String overvoltageVarningSign;
    //  欠压报警标志
    private String undervoltageVarningSign;
    //  过流报警标志
    private String overcurrentVarningSign;
    //  欠流报警标志
    private String undercurrentWarningSign;
    //  漏电报警标志
    private String leakageWarningSign;
    //  灯杆倒塌报警标志
    private String lampPostCollapseWarningSign;
    //   灯杆碰撞报警标志
    private String lampPoleCollapseWarningSign;
    //   温度异常报警标志
    private String AbnormalTemperatureWarningSign;
    //     重启计数
    private String restartCount;
    //    X方向加速度初始值
    private String xfxjsdcsz;
    //     X方向加速度初始值小数
    private String xfxjsdcszxs;
    //    Y方向加速度初始值
    private String yfxjscsz;
    //      Y方向加速度初始值小数
    private String yfxjscszxs;
    // Z方向加速度初始值
    private String zfxjsdcsz;
    // Z方向加速度初始值小数
    private String zfxjsdcszxs;
    //   lora接收频道
    private String lerajspd;
    //     lora应答频道
    private String leraydpd;
    //     lora报警频道
    private String lerabjpd;
    //    经度整数
    private String jdzs;
    //    经度小数
    private String jdxs;
    //    纬度整数
    private String wdzs;
    //    纬度小数
    private String wdxs;


    public String getNc() {
        return nc;
    }

    public void setNc(String nc) {
        this.nc = nc;
    }

    public String getAngleCalibrationSign() {
        return angleCalibrationSign;
    }

    public void setAngleCalibrationSign(String angleCalibrationSign) {
        this.angleCalibrationSign = angleCalibrationSign;
    }

    public String getAngleStandards() {
        return angleStandards;
    }

    public void setAngleStandards(String angleStandards) {
        this.angleStandards = angleStandards;
    }

    public String getAngleAlarmThresholdArror() {
        return angleAlarmThresholdArror;
    }

    public void setAngleAlarmThresholdArror(String angleAlarmThresholdArror) {
        this.angleAlarmThresholdArror = angleAlarmThresholdArror;
    }

    public String getOvervoltageVarningSign() {
        return overvoltageVarningSign;
    }

    public void setOvervoltageVarningSign(String overvoltageVarningSign) {
        this.overvoltageVarningSign = overvoltageVarningSign;
    }

    public String getUndervoltageVarningSign() {
        return undervoltageVarningSign;
    }

    public void setUndervoltageVarningSign(String undervoltageVarningSign) {
        this.undervoltageVarningSign = undervoltageVarningSign;
    }

    public String getOvercurrentVarningSign() {
        return overcurrentVarningSign;
    }

    public void setOvercurrentVarningSign(String overcurrentVarningSign) {
        this.overcurrentVarningSign = overcurrentVarningSign;
    }

    public String getUndercurrentWarningSign() {
        return undercurrentWarningSign;
    }

    public void setUndercurrentWarningSign(String undercurrentWarningSign) {
        this.undercurrentWarningSign = undercurrentWarningSign;
    }

    public String getLeakageWarningSign() {
        return leakageWarningSign;
    }

    public void setLeakageWarningSign(String leakageWarningSign) {
        this.leakageWarningSign = leakageWarningSign;
    }

    public String getLampPostCollapseWarningSign() {
        return lampPostCollapseWarningSign;
    }

    public void setLampPostCollapseWarningSign(String lampPostCollapseWarningSign) {
        this.lampPostCollapseWarningSign = lampPostCollapseWarningSign;
    }

    public String getLampPoleCollapseWarningSign() {
        return lampPoleCollapseWarningSign;
    }

    public void setLampPoleCollapseWarningSign(String lampPoleCollapseWarningSign) {
        this.lampPoleCollapseWarningSign = lampPoleCollapseWarningSign;
    }

    public String getAbnormalTemperatureWarningSign() {
        return AbnormalTemperatureWarningSign;
    }

    public void setAbnormalTemperatureWarningSign(String abnormalTemperatureWarningSign) {
        AbnormalTemperatureWarningSign = abnormalTemperatureWarningSign;
    }

    public String getRestartCount() {
        return restartCount;
    }

    public void setRestartCount(String restartCount) {
        this.restartCount = restartCount;
    }

    public String getXfxjsdcsz() {
        return xfxjsdcsz;
    }

    public void setXfxjsdcsz(String xfxjsdcsz) {
        this.xfxjsdcsz = xfxjsdcsz;
    }

    public String getXfxjsdcszxs() {
        return xfxjsdcszxs;
    }

    public void setXfxjsdcszxs(String xfxjsdcszxs) {
        this.xfxjsdcszxs = xfxjsdcszxs;
    }

    public String getYfxjscsz() {
        return yfxjscsz;
    }

    public void setYfxjscsz(String yfxjscsz) {
        this.yfxjscsz = yfxjscsz;
    }


    public String getYfxjscszxs() {
        return yfxjscszxs;
    }

    public void setYfxjscszxs(String yfxjscszxs) {
        this.yfxjscszxs = yfxjscszxs;
    }

    public String getZfxjsdcsz() {
        return zfxjsdcsz;
    }

    public void setZfxjsdcsz(String zfxjsdcsz) {
        this.zfxjsdcsz = zfxjsdcsz;
    }

    public String getZfxjsdcszxs() {
        return zfxjsdcszxs;
    }

    public void setZfxjsdcszxs(String zfxjsdcszxs) {
        this.zfxjsdcszxs = zfxjsdcszxs;
    }

    public String getLerajspd() {
        return lerajspd;
    }

    public void setLerajspd(String lerajspd) {
        this.lerajspd = lerajspd;
    }

    public String getLeraydpd() {
        return leraydpd;
    }

    public void setLeraydpd(String leraydpd) {
        this.leraydpd = leraydpd;
    }

    public String getLerabjpd() {
        return lerabjpd;
    }

    public void setLerabjpd(String lerabjpd) {
        this.lerabjpd = lerabjpd;
    }

    public String getJdzs() {
        return jdzs;
    }

    public void setJdzs(String jdzs) {
        this.jdzs = jdzs;
    }

    public String getJdxs() {
        return jdxs;
    }

    public void setJdxs(String jdxs) {
        this.jdxs = jdxs;
    }

    public String getWdzs() {
        return wdzs;
    }

    public void setWdzs(String wdzs) {
        this.wdzs = wdzs;
    }

    public String getWdxs() {
        return wdxs;
    }

    public void setWdxs(String wdxs) {
        this.wdxs = wdxs;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getUpdateIndex() {
        return updateIndex;
    }

    public void setUpdateIndex(String updateIndex) {
        this.updateIndex = updateIndex;
    }

    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }

    public String getMainLight1Hour() {
        return mainLight1Hour;
    }

    public void setMainLight1Hour(String mainLight1Hour) {
        this.mainLight1Hour = mainLight1Hour;
    }

    public String getMainLight2Hour() {
        return mainLight2Hour;
    }

    public void setMainLight2Hour(String mainLight2Hour) {
        this.mainLight2Hour = mainLight2Hour;
    }

    public String getMainLight3Hour() {
        return mainLight3Hour;
    }

    public void setMainLight3Hour(String mainLight3Hour) {
        this.mainLight3Hour = mainLight3Hour;
    }

    public String getMainLight4Hour() {
        return mainLight4Hour;
    }

    public void setMainLight4Hour(String mainLight4Hour) {
        this.mainLight4Hour = mainLight4Hour;
    }

    public String getMainLight5Hour() {
        return mainLight5Hour;
    }

    public void setMainLight5Hour(String mainLight5Hour) {
        this.mainLight5Hour = mainLight5Hour;
    }

    public String getMainLight6Hour() {
        return mainLight6Hour;
    }

    public void setMainLight6Hour(String mainLight6Hour) {
        this.mainLight6Hour = mainLight6Hour;
    }

    public String getMainLight1Minute() {
        return mainLight1Minute;
    }

    public void setMainLight1Minute(String mainLight1Minute) {
        this.mainLight1Minute = mainLight1Minute;
    }

    public String getMainLight2Minute() {
        return mainLight2Minute;
    }

    public void setMainLight2Minute(String mainLight2Minute) {
        this.mainLight2Minute = mainLight2Minute;
    }

    public String getMainLight3Minute() {
        return mainLight3Minute;
    }

    public void setMainLight3Minute(String mainLight3Minute) {
        this.mainLight3Minute = mainLight3Minute;
    }

    public String getMainLight4Minute() {
        return mainLight4Minute;
    }

    public void setMainLight4Minute(String mainLight4Minute) {
        this.mainLight4Minute = mainLight4Minute;
    }

    public String getMainLight5Minute() {
        return mainLight5Minute;
    }

    public void setMainLight5Minute(String mainLight5Minute) {
        this.mainLight5Minute = mainLight5Minute;
    }

    public String getMainLight6Minute() {
        return mainLight6Minute;
    }

    public void setMainLight6Minute(String mainLight6Minute) {
        this.mainLight6Minute = mainLight6Minute;
    }

    public String getMainLight1Brightness() {
        return mainLight1Brightness;
    }

    public void setMainLight1Brightness(String mainLight1Brightness) {
        this.mainLight1Brightness = mainLight1Brightness;
    }

    public String getMainLight2Brightness() {
        return mainLight2Brightness;
    }

    public void setMainLight2Brightness(String mainLight2Brightness) {
        this.mainLight2Brightness = mainLight2Brightness;
    }

    public String getMainLight3Brightness() {
        return mainLight3Brightness;
    }

    public void setMainLight3Brightness(String mainLight3Brightness) {
        this.mainLight3Brightness = mainLight3Brightness;
    }

    public String getMainLight4Brightness() {
        return mainLight4Brightness;
    }

    public void setMainLight4Brightness(String mainLight4Brightness) {
        this.mainLight4Brightness = mainLight4Brightness;
    }

    public String getMainLight5Brightness() {
        return mainLight5Brightness;
    }

    public void setMainLight5Brightness(String mainLight5Brightness) {
        this.mainLight5Brightness = mainLight5Brightness;
    }

    public String getMainLight6Brightness() {
        return mainLight6Brightness;
    }

    public void setMainLight6Brightness(String mainLight6Brightness) {
        this.mainLight6Brightness = mainLight6Brightness;
    }

    public String getAuxiliaryLight1Hour() {
        return auxiliaryLight1Hour;
    }

    public void setAuxiliaryLight1Hour(String auxiliaryLight1Hour) {
        this.auxiliaryLight1Hour = auxiliaryLight1Hour;
    }

    public String getAuxiliaryLight2Hour() {
        return auxiliaryLight2Hour;
    }

    public void setAuxiliaryLight2Hour(String auxiliaryLight2Hour) {
        this.auxiliaryLight2Hour = auxiliaryLight2Hour;
    }

    public String getAuxiliaryLight3Hour() {
        return auxiliaryLight3Hour;
    }

    public void setAuxiliaryLight3Hour(String auxiliaryLight3Hour) {
        this.auxiliaryLight3Hour = auxiliaryLight3Hour;
    }

    public String getAuxiliaryLight4Hour() {
        return auxiliaryLight4Hour;
    }

    public void setAuxiliaryLight4Hour(String auxiliaryLight4Hour) {
        this.auxiliaryLight4Hour = auxiliaryLight4Hour;
    }

    public String getAuxiliaryLight5Hour() {
        return auxiliaryLight5Hour;
    }

    public void setAuxiliaryLight5Hour(String auxiliaryLight5Hour) {
        this.auxiliaryLight5Hour = auxiliaryLight5Hour;
    }

    public String getAuxiliaryLight6Hour() {
        return auxiliaryLight6Hour;
    }

    public void setAuxiliaryLight6Hour(String auxiliaryLight6Hour) {
        this.auxiliaryLight6Hour = auxiliaryLight6Hour;
    }

    public String getAuxiliaryLight1Minute() {
        return auxiliaryLight1Minute;
    }

    public void setAuxiliaryLight1Minute(String auxiliaryLight1Minute) {
        this.auxiliaryLight1Minute = auxiliaryLight1Minute;
    }

    public String getAuxiliaryLight2Minute() {
        return auxiliaryLight2Minute;
    }

    public void setAuxiliaryLight2Minute(String auxiliaryLight2Minute) {
        this.auxiliaryLight2Minute = auxiliaryLight2Minute;
    }

    public String getAuxiliaryLight3Minute() {
        return auxiliaryLight3Minute;
    }

    public void setAuxiliaryLight3Minute(String auxiliaryLight3Minute) {
        this.auxiliaryLight3Minute = auxiliaryLight3Minute;
    }

    public String getAuxiliaryLight4Minute() {
        return auxiliaryLight4Minute;
    }

    public void setAuxiliaryLight4Minute(String auxiliaryLight4Minute) {
        this.auxiliaryLight4Minute = auxiliaryLight4Minute;
    }

    public String getAuxiliaryLight5Minute() {
        return auxiliaryLight5Minute;
    }

    public void setAuxiliaryLight5Minute(String auxiliaryLight5Minute) {
        this.auxiliaryLight5Minute = auxiliaryLight5Minute;
    }

    public String getAuxiliaryLight6Minute() {
        return auxiliaryLight6Minute;
    }

    public void setAuxiliaryLight6Minute(String auxiliaryLight6Minute) {
        this.auxiliaryLight6Minute = auxiliaryLight6Minute;
    }

    public String getAuxiliaryLight1Brightness() {
        return auxiliaryLight1Brightness;
    }

    public void setAuxiliaryLight1Brightness(String auxiliaryLight1Brightness) {
        this.auxiliaryLight1Brightness = auxiliaryLight1Brightness;
    }

    public String getAuxiliaryLight2Brightness() {
        return auxiliaryLight2Brightness;
    }

    public void setAuxiliaryLight2Brightness(String auxiliaryLight2Brightness) {
        this.auxiliaryLight2Brightness = auxiliaryLight2Brightness;
    }

    public String getAuxiliaryLight3Brightness() {
        return auxiliaryLight3Brightness;
    }

    public void setAuxiliaryLight3Brightness(String auxiliaryLight3Brightness) {
        this.auxiliaryLight3Brightness = auxiliaryLight3Brightness;
    }

    public String getAuxiliaryLight4Brightness() {
        return auxiliaryLight4Brightness;
    }

    public void setAuxiliaryLight4Brightness(String auxiliaryLight4Brightness) {
        this.auxiliaryLight4Brightness = auxiliaryLight4Brightness;
    }

    public String getAuxiliaryLight5Brightness() {
        return auxiliaryLight5Brightness;
    }

    public void setAuxiliaryLight5Brightness(String auxiliaryLight5Brightness) {
        this.auxiliaryLight5Brightness = auxiliaryLight5Brightness;
    }

    public String getAuxiliaryLight6Brightness() {
        return auxiliaryLight6Brightness;
    }

    public void setAuxiliaryLight6Brightness(String auxiliaryLight6Brightness) {
        this.auxiliaryLight6Brightness = auxiliaryLight6Brightness;
    }

    public String getOvercurrentProtectionWwitch() {
        return overcurrentProtectionWwitch;
    }

    public void setOvercurrentProtectionWwitch(String overcurrentProtectionWwitch) {
        this.overcurrentProtectionWwitch = overcurrentProtectionWwitch;
    }

    public String getEarthLeakageCircuitBreaker() {
        return earthLeakageCircuitBreaker;
    }

    public void setEarthLeakageCircuitBreaker(String earthLeakageCircuitBreaker) {
        this.earthLeakageCircuitBreaker = earthLeakageCircuitBreaker;
    }

    public String getIlluminationLightSwitch() {
        return illuminationLightSwitch;
    }

    public void setIlluminationLightSwitch(String illuminationLightSwitch) {
        this.illuminationLightSwitch = illuminationLightSwitch;
    }

    public String getOvervoltageProtectionThreshold() {
        return overvoltageProtectionThreshold;
    }

    public void setOvervoltageProtectionThreshold(String overvoltageProtectionThreshold) {
        this.overvoltageProtectionThreshold = overvoltageProtectionThreshold;
    }

    public String getUndervoltageProtectionThreshold() {
        return undervoltageProtectionThreshold;
    }

    public void setUndervoltageProtectionThreshold(String undervoltageProtectionThreshold) {
        this.undervoltageProtectionThreshold = undervoltageProtectionThreshold;
    }

    public String getOvercurrentProtectionThreshold() {
        return overcurrentProtectionThreshold;
    }

    public void setOvercurrentProtectionThreshold(String overcurrentProtectionThreshold) {
        this.overcurrentProtectionThreshold = overcurrentProtectionThreshold;
    }

    public String getUndercurrentProtectionThreshold() {
        return undercurrentProtectionThreshold;
    }

    public void setUndercurrentProtectionThreshold(String undercurrentProtectionThreshold) {
        this.undercurrentProtectionThreshold = undercurrentProtectionThreshold;
    }

    public String getAlarmSwitch() {
        return alarmSwitch;
    }

    public void setAlarmSwitch(String alarmSwitch) {
        this.alarmSwitch = alarmSwitch;
    }

    public String getLeakageProtectionThreshold() {
        return leakageProtectionThreshold;
    }

    public void setLeakageProtectionThreshold(String leakageProtectionThreshold) {
        this.leakageProtectionThreshold = leakageProtectionThreshold;
    }

    public String getIlluminationLightThreshold() {
        return illuminationLightThreshold;
    }

    public void setIlluminationLightThreshold(String illuminationLightThreshold) {
        this.illuminationLightThreshold = illuminationLightThreshold;
    }

    public String getLightOffThreshold() {
        return lightOffThreshold;
    }

    public void setLightOffThreshold(String lightOffThreshold) {
        this.lightOffThreshold = lightOffThreshold;
    }

    public String getLampPoleCollapseAlarmSwitch() {
        return LampPoleCollapseAlarmSwitch;
    }

    public void setLampPoleCollapseAlarmSwitch(String lampPoleCollapseAlarmSwitch) {
        LampPoleCollapseAlarmSwitch = lampPoleCollapseAlarmSwitch;
    }

    public String getProjectArea() {
        return projectArea;
    }

    public void setProjectArea(String projectArea) {
        this.projectArea = projectArea;
    }

    public String getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    public String getImei() {
        return Imei;
    }

    public void setImei(String imei) {
        Imei = imei;
    }

    public String getMaintainImei() {
        return maintainImei;
    }

    public void setMaintainImei(String maintainImei) {
        this.maintainImei = maintainImei;
    }

    public String getBaseplateId() {
        return baseplateId;
    }

    public void setBaseplateId(String baseplateId) {
        this.baseplateId = baseplateId;
    }

    @Override
    public String toString() {
        return "NfcDeviceInfo{" +
                "deviceType='" + deviceType + '\'' +
                ", updateIndex='" + updateIndex + '\'' +
                ", crc='" + crc + '\'' +
                ", mainLight1Hour='" + mainLight1Hour + '\'' +
                ", mainLight2Hour='" + mainLight2Hour + '\'' +
                ", mainLight3Hour='" + mainLight3Hour + '\'' +
                ", mainLight4Hour='" + mainLight4Hour + '\'' +
                ", mainLight5Hour='" + mainLight5Hour + '\'' +
                ", mainLight6Hour='" + mainLight6Hour + '\'' +
                ", mainLight1Minute='" + mainLight1Minute + '\'' +
                ", mainLight2Minute='" + mainLight2Minute + '\'' +
                ", mainLight3Minute='" + mainLight3Minute + '\'' +
                ", mainLight4Minute='" + mainLight4Minute + '\'' +
                ", mainLight5Minute='" + mainLight5Minute + '\'' +
                ", mainLight6Minute='" + mainLight6Minute + '\'' +
                ", mainLight1Brightness='" + mainLight1Brightness + '\'' +
                ", mainLight2Brightness='" + mainLight2Brightness + '\'' +
                ", mainLight3Brightness='" + mainLight3Brightness + '\'' +
                ", mainLight4Brightness='" + mainLight4Brightness + '\'' +
                ", mainLight5Brightness='" + mainLight5Brightness + '\'' +
                ", mainLight6Brightness='" + mainLight6Brightness + '\'' +
                ", auxiliaryLight1Hour='" + auxiliaryLight1Hour + '\'' +
                ", auxiliaryLight2Hour='" + auxiliaryLight2Hour + '\'' +
                ", auxiliaryLight3Hour='" + auxiliaryLight3Hour + '\'' +
                ", auxiliaryLight4Hour='" + auxiliaryLight4Hour + '\'' +
                ", auxiliaryLight5Hour='" + auxiliaryLight5Hour + '\'' +
                ", auxiliaryLight6Hour='" + auxiliaryLight6Hour + '\'' +
                ", auxiliaryLight1Minute='" + auxiliaryLight1Minute + '\'' +
                ", auxiliaryLight2Minute='" + auxiliaryLight2Minute + '\'' +
                ", auxiliaryLight3Minute='" + auxiliaryLight3Minute + '\'' +
                ", auxiliaryLight4Minute='" + auxiliaryLight4Minute + '\'' +
                ", auxiliaryLight5Minute='" + auxiliaryLight5Minute + '\'' +
                ", auxiliaryLight6Minute='" + auxiliaryLight6Minute + '\'' +
                ", auxiliaryLight1Brightness='" + auxiliaryLight1Brightness + '\'' +
                ", auxiliaryLight2Brightness='" + auxiliaryLight2Brightness + '\'' +
                ", auxiliaryLight3Brightness='" + auxiliaryLight3Brightness + '\'' +
                ", auxiliaryLight4Brightness='" + auxiliaryLight4Brightness + '\'' +
                ", auxiliaryLight5Brightness='" + auxiliaryLight5Brightness + '\'' +
                ", auxiliaryLight6Brightness='" + auxiliaryLight6Brightness + '\'' +
                ", overcurrentProtectionWwitch='" + overcurrentProtectionWwitch + '\'' +
                ", earthLeakageCircuitBreaker='" + earthLeakageCircuitBreaker + '\'' +
                ", illuminationLightSwitch='" + illuminationLightSwitch + '\'' +
                ", overvoltageProtectionThreshold='" + overvoltageProtectionThreshold + '\'' +
                ", undervoltageProtectionThreshold='" + undervoltageProtectionThreshold + '\'' +
                ", overcurrentProtectionThreshold='" + overcurrentProtectionThreshold + '\'' +
                ", undercurrentProtectionThreshold='" + undercurrentProtectionThreshold + '\'' +
                ", alarmSwitch='" + alarmSwitch + '\'' +
                ", leakageProtectionThreshold='" + leakageProtectionThreshold + '\'' +
                ", illuminationLightThreshold='" + illuminationLightThreshold + '\'' +
                ", lightOffThreshold='" + lightOffThreshold + '\'' +
                ", LampPoleCollapseAlarmSwitch='" + LampPoleCollapseAlarmSwitch + '\'' +
                ", projectArea='" + projectArea + '\'' +
                ", projectNumber='" + projectNumber + '\'' +
                ", Imei='" + Imei + '\'' +
                ", maintainImei='" + maintainImei + '\'' +
                ", baseplateId='" + baseplateId + '\'' +
                '}';
    }
}
