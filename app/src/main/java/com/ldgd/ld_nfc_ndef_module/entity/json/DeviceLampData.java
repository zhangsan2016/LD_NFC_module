package com.ldgd.ld_nfc_ndef_module.entity.json;

import java.util.List;

/**
 * Created by ldgd on 2021/6/26.
 */

public class DeviceLampData {


    /**
     * errno : 0
     * errmsg :
     * data : {"count":1,"totalPages":1,"pageSize":2000,"currentPage":1,"data":[{"UUID":"2015BA11A0000B0000000019","LAT":"22.635641","LNG":"114.004099","NAME":"洛丁展厅19号灯","TYPE":2,"PROJECT":"中科洛丁展示项目/深圳展厅","SUBGROUP":"","ADDR":"","IPADDR":"","_id":28,"FUUID":"77,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99","PUUID":"","subgroups":"[\"1\",\"2\"]","admin":"ld","code":"518100","members":"[\"ld\",\"chenyong\"]","config":null,"report_config":null,"ts_created":"2021-03-17T05:26:45.000Z","ts_updated":"2021-06-26T02:20:30.000Z","Power_Manufacturer":"英飞特","Lamp_Manufacturer":"77","Energy":74161.94,"FirDimming":20,"Illu":3838.54,"Power":62.58,"STATE":1,"Temp":62.4,"Warning_state":0,"ts_mqtt":1624678847633,"_countBad":0,"_countBadCurrent":0,"_countBadLeak":0,"_countBadTemp":0,"_countBadVoltage":0,"_countMessage":0,"_lastStatsDate":"2021-06-24T16:00:00.000Z","__countMessage":0,"Fif_tp_Fir":20,"Fif_tp_Sec":20,"Fif_tt_Fir":"4:30","Fif_tt_Sec":"4:30","Fir_tp_Fir":80,"Fir_tp_Sec":30,"Fir_tt_Fir":"19:30","Fir_tt_Sec":"19:30","Four_tp_Fir":20,"Four_tp_Sec":20,"Four_tt_Fir":"1:00","Four_tt_Sec":"1:00","Sec_tp_Fir":80,"Sec_tp_Sec":30,"Sec_tt_Fir":"21:00","Sec_tt_Sec":"21:00","Six_tp_Fir":0,"Six_tp_Sec":0,"Six_tt_Fir":"7:00","Six_tt_Sec":"7:00","Thir_tp_Fir":20,"Thir_tp_Sec":20,"Thir_tt_Fir":"23:30","Thir_tt_Sec":"23:30","Current":0.27,"Voltage":227.79,"C_downthreshold":0.01,"C_upthreshold":5,"Leak_c_enable":1,"Leak_c_threshold":20,"V_downthreshold":120,"V_upthreshold":260,"_countOn":0,"_countBadOn":0,"_countBadOff":0,"_countOffline":0,"energy":0}]}
     */

    private int errno;
    private String errmsg;
    private DataBeanX data;

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

    public static class DataBeanX {
        /**
         * count : 1
         * totalPages : 1
         * pageSize : 2000
         * currentPage : 1
         * data : [{"UUID":"2015BA11A0000B0000000019","LAT":"22.635641","LNG":"114.004099","NAME":"洛丁展厅19号灯","TYPE":2,"PROJECT":"中科洛丁展示项目/深圳展厅","SUBGROUP":"","ADDR":"","IPADDR":"","_id":28,"FUUID":"77,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99","PUUID":"","subgroups":"[\"1\",\"2\"]","admin":"ld","code":"518100","members":"[\"ld\",\"chenyong\"]","config":null,"report_config":null,"ts_created":"2021-03-17T05:26:45.000Z","ts_updated":"2021-06-26T02:20:30.000Z","Power_Manufacturer":"英飞特","Lamp_Manufacturer":"77","Energy":74161.94,"FirDimming":20,"Illu":3838.54,"Power":62.58,"STATE":1,"Temp":62.4,"Warning_state":0,"ts_mqtt":1624678847633,"_countBad":0,"_countBadCurrent":0,"_countBadLeak":0,"_countBadTemp":0,"_countBadVoltage":0,"_countMessage":0,"_lastStatsDate":"2021-06-24T16:00:00.000Z","__countMessage":0,"Fif_tp_Fir":20,"Fif_tp_Sec":20,"Fif_tt_Fir":"4:30","Fif_tt_Sec":"4:30","Fir_tp_Fir":80,"Fir_tp_Sec":30,"Fir_tt_Fir":"19:30","Fir_tt_Sec":"19:30","Four_tp_Fir":20,"Four_tp_Sec":20,"Four_tt_Fir":"1:00","Four_tt_Sec":"1:00","Sec_tp_Fir":80,"Sec_tp_Sec":30,"Sec_tt_Fir":"21:00","Sec_tt_Sec":"21:00","Six_tp_Fir":0,"Six_tp_Sec":0,"Six_tt_Fir":"7:00","Six_tt_Sec":"7:00","Thir_tp_Fir":20,"Thir_tp_Sec":20,"Thir_tt_Fir":"23:30","Thir_tt_Sec":"23:30","Current":0.27,"Voltage":227.79,"C_downthreshold":0.01,"C_upthreshold":5,"Leak_c_enable":1,"Leak_c_threshold":20,"V_downthreshold":120,"V_upthreshold":260,"_countOn":0,"_countBadOn":0,"_countBadOff":0,"_countOffline":0,"energy":0}]
         */

        private int count;
        private int totalPages;
        private int pageSize;
        private int currentPage;
        private List<DataBean> data;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * UUID : 2015BA11A0000B0000000019
             * LAT : 22.635641
             * LNG : 114.004099
             * NAME : 洛丁展厅19号灯
             * TYPE : 2
             * PROJECT : 中科洛丁展示项目/深圳展厅
             * SUBGROUP :
             * ADDR :
             * IPADDR :
             * _id : 28
             * FUUID : 77,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99
             * PUUID :
             * subgroups : ["1","2"]
             * admin : ld
             * code : 518100
             * members : ["ld","chenyong"]
             * config : null
             * report_config : null
             * ts_created : 2021-03-17T05:26:45.000Z
             * ts_updated : 2021-06-26T02:20:30.000Z
             * Power_Manufacturer : 英飞特
             * Lamp_Manufacturer : 77
             * Energy : 74161.94
             * FirDimming : 20
             * Illu : 3838.54
             * Power : 62.58
             * STATE : 1
             * Temp : 62.4
             * Warning_state : 0
             * ts_mqtt : 1624678847633
             * _countBad : 0
             * _countBadCurrent : 0
             * _countBadLeak : 0
             * _countBadTemp : 0
             * _countBadVoltage : 0
             * _countMessage : 0
             * _lastStatsDate : 2021-06-24T16:00:00.000Z
             * __countMessage : 0
             * Fif_tp_Fir : 20
             * Fif_tp_Sec : 20
             * Fif_tt_Fir : 4:30
             * Fif_tt_Sec : 4:30
             * Fir_tp_Fir : 80
             * Fir_tp_Sec : 30
             * Fir_tt_Fir : 19:30
             * Fir_tt_Sec : 19:30
             * Four_tp_Fir : 20
             * Four_tp_Sec : 20
             * Four_tt_Fir : 1:00
             * Four_tt_Sec : 1:00
             * Sec_tp_Fir : 80
             * Sec_tp_Sec : 30
             * Sec_tt_Fir : 21:00
             * Sec_tt_Sec : 21:00
             * Six_tp_Fir : 0
             * Six_tp_Sec : 0
             * Six_tt_Fir : 7:00
             * Six_tt_Sec : 7:00
             * Thir_tp_Fir : 20
             * Thir_tp_Sec : 20
             * Thir_tt_Fir : 23:30
             * Thir_tt_Sec : 23:30
             * Current : 0.27
             * Voltage : 227.79
             * C_downthreshold : 0.01
             * C_upthreshold : 5
             * Leak_c_enable : 1
             * Leak_c_threshold : 20
             * V_downthreshold : 120
             * V_upthreshold : 260
             * _countOn : 0
             * _countBadOn : 0
             * _countBadOff : 0
             * _countOffline : 0
             * energy : 0
             */

            private String UUID;
            private String LAT;
            private String LNG;
            private String NAME;
            private int TYPE;
            private String PROJECT;
            private String SUBGROUP;
            private String ADDR;
            private String IPADDR;
            private int _id;
            private String FUUID;
            private String PUUID;
            private String subgroups;
            private String admin;
            private String code;
            private String members;
            private Object config;
            private Object report_config;
            private String ts_created;
            private String ts_updated;
            private String Power_Manufacturer;
            private String Lamp_Manufacturer;
            private double Energy;
            private int FirDimming;
            private double Illu;
            private double Power;
            private int STATE;
            private double Temp;
            private int Warning_state;
            private long ts_mqtt;
            private int _countBad;
            private int _countBadCurrent;
            private int _countBadLeak;
            private int _countBadTemp;
            private int _countBadVoltage;
            private int _countMessage;
            private String _lastStatsDate;
            private int __countMessage;
            private int Fif_tp_Fir;
            private int Fif_tp_Sec;
            private String Fif_tt_Fir;
            private String Fif_tt_Sec;
            private int Fir_tp_Fir;
            private int Fir_tp_Sec;
            private String Fir_tt_Fir;
            private String Fir_tt_Sec;
            private int Four_tp_Fir;
            private int Four_tp_Sec;
            private String Four_tt_Fir;
            private String Four_tt_Sec;
            private int Sec_tp_Fir;
            private int Sec_tp_Sec;
            private String Sec_tt_Fir;
            private String Sec_tt_Sec;
            private int Six_tp_Fir;
            private int Six_tp_Sec;
            private String Six_tt_Fir;
            private String Six_tt_Sec;
            private int Thir_tp_Fir;
            private int Thir_tp_Sec;
            private String Thir_tt_Fir;
            private String Thir_tt_Sec;
            private double Current;
            private double Voltage;
            private double C_downthreshold;
            private int C_upthreshold;
            private int Leak_c_enable;
            private int Leak_c_threshold;
            private int V_downthreshold;
            private int V_upthreshold;
            private int _countOn;
            private int _countBadOn;
            private int _countBadOff;
            private int _countOffline;
            private int energy;

            public String getUUID() {
                return UUID;
            }

            public void setUUID(String UUID) {
                this.UUID = UUID;
            }

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

            public int getTYPE() {
                return TYPE;
            }

            public void setTYPE(int TYPE) {
                this.TYPE = TYPE;
            }

            public String getPROJECT() {
                return PROJECT;
            }

            public void setPROJECT(String PROJECT) {
                this.PROJECT = PROJECT;
            }

            public String getSUBGROUP() {
                return SUBGROUP;
            }

            public void setSUBGROUP(String SUBGROUP) {
                this.SUBGROUP = SUBGROUP;
            }

            public String getADDR() {
                return ADDR;
            }

            public void setADDR(String ADDR) {
                this.ADDR = ADDR;
            }

            public String getIPADDR() {
                return IPADDR;
            }

            public void setIPADDR(String IPADDR) {
                this.IPADDR = IPADDR;
            }

            public int get_id() {
                return _id;
            }

            public void set_id(int _id) {
                this._id = _id;
            }

            public String getFUUID() {
                return FUUID;
            }

            public void setFUUID(String FUUID) {
                this.FUUID = FUUID;
            }

            public String getPUUID() {
                return PUUID;
            }

            public void setPUUID(String PUUID) {
                this.PUUID = PUUID;
            }

            public String getSubgroups() {
                return subgroups;
            }

            public void setSubgroups(String subgroups) {
                this.subgroups = subgroups;
            }

            public String getAdmin() {
                return admin;
            }

            public void setAdmin(String admin) {
                this.admin = admin;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getMembers() {
                return members;
            }

            public void setMembers(String members) {
                this.members = members;
            }

            public Object getConfig() {
                return config;
            }

            public void setConfig(Object config) {
                this.config = config;
            }

            public Object getReport_config() {
                return report_config;
            }

            public void setReport_config(Object report_config) {
                this.report_config = report_config;
            }

            public String getTs_created() {
                return ts_created;
            }

            public void setTs_created(String ts_created) {
                this.ts_created = ts_created;
            }

            public String getTs_updated() {
                return ts_updated;
            }

            public void setTs_updated(String ts_updated) {
                this.ts_updated = ts_updated;
            }

            public String getPower_Manufacturer() {
                return Power_Manufacturer;
            }

            public void setPower_Manufacturer(String Power_Manufacturer) {
                this.Power_Manufacturer = Power_Manufacturer;
            }

            public String getLamp_Manufacturer() {
                return Lamp_Manufacturer;
            }

            public void setLamp_Manufacturer(String Lamp_Manufacturer) {
                this.Lamp_Manufacturer = Lamp_Manufacturer;
            }


            public void setEnergy(double Energy) {
                this.Energy = Energy;
            }

            public int getFirDimming() {
                return FirDimming;
            }

            public void setFirDimming(int FirDimming) {
                this.FirDimming = FirDimming;
            }

            public double getIllu() {
                return Illu;
            }

            public void setIllu(double Illu) {
                this.Illu = Illu;
            }

            public double getPower() {
                return Power;
            }

            public void setPower(double Power) {
                this.Power = Power;
            }

            public int getSTATE() {
                return STATE;
            }

            public void setSTATE(int STATE) {
                this.STATE = STATE;
            }

            public double getTemp() {
                return Temp;
            }

            public void setTemp(double Temp) {
                this.Temp = Temp;
            }

            public int getWarning_state() {
                return Warning_state;
            }

            public void setWarning_state(int Warning_state) {
                this.Warning_state = Warning_state;
            }

            public long getTs_mqtt() {
                return ts_mqtt;
            }

            public void setTs_mqtt(long ts_mqtt) {
                this.ts_mqtt = ts_mqtt;
            }

            public int get_countBad() {
                return _countBad;
            }

            public void set_countBad(int _countBad) {
                this._countBad = _countBad;
            }

            public int get_countBadCurrent() {
                return _countBadCurrent;
            }

            public void set_countBadCurrent(int _countBadCurrent) {
                this._countBadCurrent = _countBadCurrent;
            }

            public int get_countBadLeak() {
                return _countBadLeak;
            }

            public void set_countBadLeak(int _countBadLeak) {
                this._countBadLeak = _countBadLeak;
            }

            public int get_countBadTemp() {
                return _countBadTemp;
            }

            public void set_countBadTemp(int _countBadTemp) {
                this._countBadTemp = _countBadTemp;
            }

            public int get_countBadVoltage() {
                return _countBadVoltage;
            }

            public void set_countBadVoltage(int _countBadVoltage) {
                this._countBadVoltage = _countBadVoltage;
            }

            public int get_countMessage() {
                return _countMessage;
            }

            public void set_countMessage(int _countMessage) {
                this._countMessage = _countMessage;
            }

            public String get_lastStatsDate() {
                return _lastStatsDate;
            }

            public void set_lastStatsDate(String _lastStatsDate) {
                this._lastStatsDate = _lastStatsDate;
            }

            public int get__countMessage() {
                return __countMessage;
            }

            public void set__countMessage(int __countMessage) {
                this.__countMessage = __countMessage;
            }

            public int getFif_tp_Fir() {
                return Fif_tp_Fir;
            }

            public void setFif_tp_Fir(int Fif_tp_Fir) {
                this.Fif_tp_Fir = Fif_tp_Fir;
            }

            public int getFif_tp_Sec() {
                return Fif_tp_Sec;
            }

            public void setFif_tp_Sec(int Fif_tp_Sec) {
                this.Fif_tp_Sec = Fif_tp_Sec;
            }

            public String getFif_tt_Fir() {
                return Fif_tt_Fir;
            }

            public void setFif_tt_Fir(String Fif_tt_Fir) {
                this.Fif_tt_Fir = Fif_tt_Fir;
            }

            public String getFif_tt_Sec() {
                return Fif_tt_Sec;
            }

            public void setFif_tt_Sec(String Fif_tt_Sec) {
                this.Fif_tt_Sec = Fif_tt_Sec;
            }

            public int getFir_tp_Fir() {
                return Fir_tp_Fir;
            }

            public void setFir_tp_Fir(int Fir_tp_Fir) {
                this.Fir_tp_Fir = Fir_tp_Fir;
            }

            public int getFir_tp_Sec() {
                return Fir_tp_Sec;
            }

            public void setFir_tp_Sec(int Fir_tp_Sec) {
                this.Fir_tp_Sec = Fir_tp_Sec;
            }

            public String getFir_tt_Fir() {
                return Fir_tt_Fir;
            }

            public void setFir_tt_Fir(String Fir_tt_Fir) {
                this.Fir_tt_Fir = Fir_tt_Fir;
            }

            public String getFir_tt_Sec() {
                return Fir_tt_Sec;
            }

            public void setFir_tt_Sec(String Fir_tt_Sec) {
                this.Fir_tt_Sec = Fir_tt_Sec;
            }

            public int getFour_tp_Fir() {
                return Four_tp_Fir;
            }

            public void setFour_tp_Fir(int Four_tp_Fir) {
                this.Four_tp_Fir = Four_tp_Fir;
            }

            public int getFour_tp_Sec() {
                return Four_tp_Sec;
            }

            public void setFour_tp_Sec(int Four_tp_Sec) {
                this.Four_tp_Sec = Four_tp_Sec;
            }

            public String getFour_tt_Fir() {
                return Four_tt_Fir;
            }

            public void setFour_tt_Fir(String Four_tt_Fir) {
                this.Four_tt_Fir = Four_tt_Fir;
            }

            public String getFour_tt_Sec() {
                return Four_tt_Sec;
            }

            public void setFour_tt_Sec(String Four_tt_Sec) {
                this.Four_tt_Sec = Four_tt_Sec;
            }

            public int getSec_tp_Fir() {
                return Sec_tp_Fir;
            }

            public void setSec_tp_Fir(int Sec_tp_Fir) {
                this.Sec_tp_Fir = Sec_tp_Fir;
            }

            public int getSec_tp_Sec() {
                return Sec_tp_Sec;
            }

            public void setSec_tp_Sec(int Sec_tp_Sec) {
                this.Sec_tp_Sec = Sec_tp_Sec;
            }

            public String getSec_tt_Fir() {
                return Sec_tt_Fir;
            }

            public void setSec_tt_Fir(String Sec_tt_Fir) {
                this.Sec_tt_Fir = Sec_tt_Fir;
            }

            public String getSec_tt_Sec() {
                return Sec_tt_Sec;
            }

            public void setSec_tt_Sec(String Sec_tt_Sec) {
                this.Sec_tt_Sec = Sec_tt_Sec;
            }

            public int getSix_tp_Fir() {
                return Six_tp_Fir;
            }

            public void setSix_tp_Fir(int Six_tp_Fir) {
                this.Six_tp_Fir = Six_tp_Fir;
            }

            public int getSix_tp_Sec() {
                return Six_tp_Sec;
            }

            public void setSix_tp_Sec(int Six_tp_Sec) {
                this.Six_tp_Sec = Six_tp_Sec;
            }

            public String getSix_tt_Fir() {
                return Six_tt_Fir;
            }

            public void setSix_tt_Fir(String Six_tt_Fir) {
                this.Six_tt_Fir = Six_tt_Fir;
            }

            public String getSix_tt_Sec() {
                return Six_tt_Sec;
            }

            public void setSix_tt_Sec(String Six_tt_Sec) {
                this.Six_tt_Sec = Six_tt_Sec;
            }

            public int getThir_tp_Fir() {
                return Thir_tp_Fir;
            }

            public void setThir_tp_Fir(int Thir_tp_Fir) {
                this.Thir_tp_Fir = Thir_tp_Fir;
            }

            public int getThir_tp_Sec() {
                return Thir_tp_Sec;
            }

            public void setThir_tp_Sec(int Thir_tp_Sec) {
                this.Thir_tp_Sec = Thir_tp_Sec;
            }

            public String getThir_tt_Fir() {
                return Thir_tt_Fir;
            }

            public void setThir_tt_Fir(String Thir_tt_Fir) {
                this.Thir_tt_Fir = Thir_tt_Fir;
            }

            public String getThir_tt_Sec() {
                return Thir_tt_Sec;
            }

            public void setThir_tt_Sec(String Thir_tt_Sec) {
                this.Thir_tt_Sec = Thir_tt_Sec;
            }

            public double getCurrent() {
                return Current;
            }

            public void setCurrent(double Current) {
                this.Current = Current;
            }

            public double getVoltage() {
                return Voltage;
            }

            public void setVoltage(double Voltage) {
                this.Voltage = Voltage;
            }

            public double getC_downthreshold() {
                return C_downthreshold;
            }

            public void setC_downthreshold(double C_downthreshold) {
                this.C_downthreshold = C_downthreshold;
            }

            public int getC_upthreshold() {
                return C_upthreshold;
            }

            public void setC_upthreshold(int C_upthreshold) {
                this.C_upthreshold = C_upthreshold;
            }

            public int getLeak_c_enable() {
                return Leak_c_enable;
            }

            public void setLeak_c_enable(int Leak_c_enable) {
                this.Leak_c_enable = Leak_c_enable;
            }

            public int getLeak_c_threshold() {
                return Leak_c_threshold;
            }

            public void setLeak_c_threshold(int Leak_c_threshold) {
                this.Leak_c_threshold = Leak_c_threshold;
            }

            public int getV_downthreshold() {
                return V_downthreshold;
            }

            public void setV_downthreshold(int V_downthreshold) {
                this.V_downthreshold = V_downthreshold;
            }

            public int getV_upthreshold() {
                return V_upthreshold;
            }

            public void setV_upthreshold(int V_upthreshold) {
                this.V_upthreshold = V_upthreshold;
            }

            public int get_countOn() {
                return _countOn;
            }

            public void set_countOn(int _countOn) {
                this._countOn = _countOn;
            }

            public int get_countBadOn() {
                return _countBadOn;
            }

            public void set_countBadOn(int _countBadOn) {
                this._countBadOn = _countBadOn;
            }

            public int get_countBadOff() {
                return _countBadOff;
            }

            public void set_countBadOff(int _countBadOff) {
                this._countBadOff = _countBadOff;
            }

            public int get_countOffline() {
                return _countOffline;
            }

            public void set_countOffline(int _countOffline) {
                this._countOffline = _countOffline;
            }

            public int getEnergy() {
                return energy;
            }

            public void setEnergy(int energy) {
                this.energy = energy;
            }
        }
    }
}
