package com.ldgd.ld_nfc_ndef_module.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.google.gson.Gson;
import com.ldgd.ld_nfc_ndef_module.R;
import com.ldgd.ld_nfc_ndef_module.base.BaseNfcActivity;
import com.ldgd.ld_nfc_ndef_module.entity.DataDictionaries;
import com.ldgd.ld_nfc_ndef_module.entity.json.DeviceLampData;
import com.ldgd.ld_nfc_ndef_module.entity.json.LampEditData;
import com.ldgd.ld_nfc_ndef_module.entity.json.LoginInfo;
import com.ldgd.ld_nfc_ndef_module.json.LoginJson;
import com.ldgd.ld_nfc_ndef_module.util.AutoFitKeyBoardUtil;
import com.ldgd.ld_nfc_ndef_module.util.BytesUtil;
import com.ldgd.ld_nfc_ndef_module.util.DrawableUtil;
import com.ldgd.ld_nfc_ndef_module.util.HttpUtil;
import com.ldgd.ld_nfc_ndef_module.util.LogUtil;
import com.ldgd.ld_nfc_ndef_module.util.MapHttpConfiguration;
import com.ldgd.ld_nfc_ndef_module.util.NfcDataUtil;
import com.ldgd.ld_nfc_ndef_module.util.NfcUtils;
import com.ldgd.ld_nfc_ndef_module.zbar.CaptureActivity;
import com.ldgd.ld_nfc_ndef_module.zbar.utils.Config;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.json.JSONException;
import org.json.JSONStringer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.ldgd.ld_nfc_ndef_module.R.id.tb_location;
import static com.ldgd.ld_nfc_ndef_module.util.NfcDataUtil.dataDictionaries;
import static com.ldgd.ld_nfc_ndef_module.util.NfcDataUtil.replaceBlank;

public class NfcNdefActivity extends BaseNfcActivity {

    private static final String TAG = "NFCActivity";
    public static final int REQUEST_CODE_QR = 10;
    // 定位返回
    public static final int REQUEST_CODE_ORIENTATION = 11;
    // 请求权限的code
    public static final int REQUEST_CODE_CAMERA = 21;

    // xml缓存的name
    private static final String NFC_DATA_CACHE = "NfcDataCache.xml";
    // xml编辑缓存的name
    private static final String NFC_EIDT_DATA_CACHE = "NfcEidtDataCache.xml";
    // handle
    private static final int HANDLE_UP_WRITE = 21;
    private static final int HANDLE_UP_READ = 22;
    private static final int START_WRITE_NFC = 23;
    private static final int STOP_WRITE_NFC = 24;


    /// static private NFCTag mTag;
    static private Tag mTag;

    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private LinearLayout ll;
    private EditText ed_search;
    private EditText et_text_editor;
    private TextView bt_save_config;
    private TextView tv_deploy;
    private TextView tv_write;
    private TextView tv_edit_switch;
    private ToggleButton tb_nfc_switch;
    private boolean temp = false;
    private Button bt_clear;
    private ProgressBar progressbar;
    private AlertDialog writeAlertDialog;
    private Button bt_uploading;
    private TextView tv_location;
    private ImageView iv_location;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    // 当前高德地图定位
    private AMapLocation cAMapLocation;
    // 登录获取的 Token
    private String token = null;

    private String cxml = "\n" +
            "<当前读取信息>\n" +
            "  <设备类型>0001</设备类型>\n" +
            "  <更新标志>58</更新标志>\n" +
            "  <CRC>0000</CRC>\n" +
            "  <安装测试模式>1</安装测试模式>\n" +
            "  <主灯1段调光时>18</主灯1段调光时>\n" +
            "  <主灯1段调光分>30</主灯1段调光分>\n" +
            "  <主灯1段调光亮度>100</主灯1段调光亮度>\n" +
            "  <主灯2段调光时>21</主灯2段调光时>\n" +
            "  <主灯2段调光分>0</主灯2段调光分>\n" +
            "  <主灯2段调光亮度>100</主灯2段调光亮度>\n" +
            "  <主灯3段调光时>23</主灯3段调光时>\n" +
            "  <主灯3段调光分>0</主灯3段调光分>\n" +
            "  <主灯3段调光亮度>100</主灯3段调光亮度>\n" +
            "  <主灯4段调光时>1</主灯4段调光时>\n" +
            "  <主灯4段调光分>0</主灯4段调光分>\n" +
            "  <主灯4段调光亮度>100</主灯4段调光亮度>\n" +
            "  <主灯5段调光时>4</主灯5段调光时>\n" +
            "  <主灯5段调光分>0</主灯5段调光分>\n" +
            "  <主灯5段调光亮度>100</主灯5段调光亮度>\n" +
            "  <主灯6段调光时>7</主灯6段调光时>\n" +
            "  <主灯6段调光分>30</主灯6段调光分>\n" +
            "  <主灯6段调光亮度>100</主灯6段调光亮度>\n" +
            "  <副灯1段调光时>18</副灯1段调光时>\n" +
            "  <副灯1段调光分>30</副灯1段调光分>\n" +
            "  <副灯1段调光亮度>100</副灯1段调光亮度>\n" +
            "  <副灯2段调光时>21</副灯2段调光时>\n" +
            "  <副灯2段调光分>0</副灯2段调光分>\n" +
            "  <副灯2段调光亮度>100</副灯2段调光亮度>\n" +
            "  <副灯3段调光时>23</副灯3段调光时>\n" +
            "  <副灯3段调光分>0</副灯3段调光分>\n" +
            "  <副灯3段调光亮度>100</副灯3段调光亮度>\n" +
            "  <副灯4段调光时>1</副灯4段调光时>\n" +
            "  <副灯4段调光分>0</副灯4段调光分>\n" +
            "  <副灯4段调光亮度>100</副灯4段调光亮度>\n" +
            "  <副灯5段调光时>4</副灯5段调光时>\n" +
            "  <副灯5段调光分>0</副灯5段调光分>\n" +
            "  <副灯5段调光亮度>100</副灯5段调光亮度>\n" +
            "  <副灯6段调光时>7</副灯6段调光时>\n" +
            "  <副灯6段调光分>30</副灯6段调光分>\n" +
            "  <副灯6段调光亮度>100</副灯6段调光亮度>\n" +
            "  <过流保护开关>1</过流保护开关>\n" +
            "  <漏电保护开关>0</漏电保护开关>\n" +
            "  <照度开灯开关>0</照度开灯开关>\n" +
            "  <过压保护阈值>250(V)</过压保护阈值>\n" +
            "  <欠压保护阈值>120(V)</欠压保护阈值>\n" +
            "  <过流保护阈值>5(A)</过流保护阈值>\n" +
            "  <欠流保护阈值>0(A)</欠流保护阈值>\n" +
            "  <报警开关>-1</报警开关>\n" +
            "  <经纬度辅助开灯开关>0</经纬度辅助开灯开关>\n" +
            "  <漏电保护阈值>40(mA)</漏电保护阈值>\n" +
            "  <照度开灯阈值>30(Lux)</照度开灯阈值>\n" +
            "  <照度关灯阈值>5(Lux)</照度关灯阈值>\n" +
            "  <灯杆倒塌报警开关>0</灯杆倒塌报警开关>\n" +
            "  <项目地区>400700</项目地区>\n" +
            "  <项目编号>00</项目编号>\n" +
            "  <IMEI>865976051596240</IMEI>\n" +
            "  <维修IMEI>0</维修IMEI>\n" +
            "  <执行底板ID>0</执行底板ID>\n" +
            "  <NC>0</NC>\n" +
            "  <角度校准标志>0</角度校准标志>\n" +
            "  <校准角度>0(度)</校准角度>\n" +
            "  <角度报警阈值误差>0</角度报警阈值误差>\n" +
            "  <过压报警标志>1</过压报警标志>\n" +
            "  <欠压报警标志>0</欠压报警标志>\n" +
            "  <过流报警标志>0</过流报警标志>\n" +
            "  <欠流报警标志>0</欠流报警标志>\n" +
            "  <漏电报警标志>0</漏电报警标志>\n" +
            "  <灯杆倒塌报警标志>0</灯杆倒塌报警标志>\n" +
            "  <灯杆碰撞报警标志>1</灯杆碰撞报警标志>\n" +
            "  <温度异常报警标志>0</温度异常报警标志>\n" +
            "  <重启计数>8(次)</重启计数>\n" +
            "  <X方向加速度初始值>0</X方向加速度初始值>\n" +
            "  <X方向加速度初始值小数>2048</X方向加速度初始值小数>\n" +
            "  <Y方向加速度初始值>0</Y方向加速度初始值>\n" +
            "  <Y方向加速度初始值小数>1536</Y方向加速度初始值小数>\n" +
            "  <Z方向加速度初始值>0</Z方向加速度初始值>\n" +
            "  <Z方向加速度初始值小数>9472</Z方向加速度初始值小数>\n" +
            "  <经度整数>0</经度整数>\n" +
            "  <经度小数>0</经度小数>\n" +
            "  <纬度整数>0</纬度整数>\n" +
            "  <纬度小数>0</纬度小数>\n" +
            "  <远程服务器域名或IP>ludeng.stgxy.com</远程服务器域名或IP>\n" +
            "  <远程端口>50001</远程端口>\n" +
            "  <用户名>device</用户名>\n" +
            "  <密码>!@Ddfox136030</密码>\n" +
            "</当前读取信息>\n";


    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case HANDLE_UP_WRITE:

                    String uuid = (String) msg.obj;
                    if (writeAlertDialog.isShowing()) {
                        TextView tv_cache_nfcuid = writeAlertDialog.findViewById(R.id.tv_write_nfcuid);
                        tv_cache_nfcuid.setText(uuid);
                    }
                    break;

                case HANDLE_UP_READ:

                    LogUtil.e("xxx HANDLE_UP_READ");
                    String uuidCache = (String) msg.obj;
                    if (writeAlertDialog.isShowing()) {
                        TextView tv_cache_nfcuid = writeAlertDialog.findViewById(R.id.tv_cache_nfcuid);
                        tv_cache_nfcuid.setText(uuidCache);
                    }

                    break;
                case START_WRITE_NFC:
                    // 初始化进度条
                    initProgressBar();
                    showProgress();

                    break;
                case STOP_WRITE_NFC:
                    // 关闭加载框
                    stopProgress();
                    break;
            }


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏顶部的状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_nfc);

        initNFC();

        initView();

        // 初始化监听
        initListening();

        //初始化定位
        initLocation();

        // 登录获取 token
        getToken();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] paramArrayOfInt) {
        super.onRequestPermissionsResult(requestCode, permissions, paramArrayOfInt);

        // 停止定位
        locationClient.stopLocation();
        // 启动定位
        locationClient.startLocation();


    }

    /**
     * 初始化定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (aMapLocation.getErrorCode() == 0) {
                    cAMapLocation = aMapLocation;

                    tv_location.setText(aMapLocation.getLongitude() + " / " + aMapLocation.getLatitude());
                    sb.append("经    度    : " + aMapLocation.getLongitude() + "\n");
                    sb.append("纬    度    : " + aMapLocation.getLatitude() + "\n");
                    Log.e("xx", ">>>>>>>>>>>>>>>>>>>>>>>>>  经纬度信息 = " + sb.toString());


                }
            }
        });
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        mOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(false); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    private void initNFC() {
        // 初始化NFC-onResume处理
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // 检测nfc权限
        NfcUtils.NfcCheck(this);

    }

    private void initListening() {

        DrawableUtil drawableUtil = new DrawableUtil(ed_search, new DrawableUtil.OnDrawableListener() {

            @Override
            public void onLeft(View v, Drawable left) {
                Toast.makeText(getApplicationContext(), "left", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRight(View v, Drawable right) {

                //动态权限申请
                if (ContextCompat.checkSelfPermission(NfcNdefActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NfcNdefActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
                } else {
                    //扫码
                    goScan();
                }

            }
        });

        tv_deploy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LogUtil.e(" xxx tv_deploy = " + et_text_editor.getText().toString().trim());
            }
        });

        // 写入
        tv_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                File file = new File(NfcNdefActivity.this.getCacheDir(), NFC_EIDT_DATA_CACHE);
                if (file.exists()) {

                    // 显示Dialog
                    writeAlertDialog.show();

                    // 清理dialog的text文本
                    TextView tv_write_nfcuid = writeAlertDialog.findViewById(R.id.tv_write_nfcuid);
                    TextView tv_read_nfcuid = writeAlertDialog.findViewById(R.id.tv_cache_nfcuid);
                    tv_write_nfcuid.setText("");
                    tv_read_nfcuid.setText("");

                    try {
                        // 解析xml文件，得到所有参数
                        FileInputStream inputStream = new FileInputStream(new File(NfcNdefActivity.this.getCacheDir(), NFC_EIDT_DATA_CACHE));
                        List<DataDictionaries> dataDictionaries = NfcDataUtil.parseXml2(inputStream);
                        String baseplateId = null;
                        for (int i = 0; i < dataDictionaries.size(); i++) {
                            if (dataDictionaries.get(i).getName().equals("执行底板ID")) {
                                baseplateId = dataDictionaries.get(i).getXmValue();
                            }
                        }

                        // 通过 Handle 更新 AlertDialog
                        Message tempMsg = myHandler.obtainMessage();
                        tempMsg.what = HANDLE_UP_READ;
                        tempMsg.obj = baseplateId.replaceAll(" +", "");
                        myHandler.sendMessage(tempMsg);

                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast("读取xml文件失败！");

                    }
                } else {
                    showToast("xml文件不存在，请先保存文件");
                }

            }
        });

        // 编辑开关切换
        tv_edit_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (temp == true) {
                    temp = false;
                    v.setBackgroundResource(R.drawable.ico_nfc_off);
                    //    et_text_editor.setEnabled(true);
                    et_text_editor.setFocusable(false);
                    et_text_editor.setCursorVisible(false);
                    et_text_editor.setFocusableInTouchMode(false);
                } else {
                    temp = true;
                    v.setBackgroundResource(R.drawable.ico_nfc_on);
                    //   et_text_editor.setEnabled(false);
                    et_text_editor.setFocusable(true);
                    et_text_editor.setCursorVisible(true);
                    et_text_editor.setFocusableInTouchMode(true);
                }
            }
        });


        // nfc切换开关
        tb_nfc_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NfcUtils.IsToSet(NfcNdefActivity.this, tb_nfc_switch);
            }
        });

        // 清除
        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearInterface();
            }
        });

        // 缓存当前数据
        bt_save_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String xmlStr = et_text_editor.getText().toString();
                if (!xmlStr.equals("")) {
                    File file = new File(NfcNdefActivity.this.getCacheDir(), NFC_EIDT_DATA_CACHE);
                    saveXmlCacheDir(xmlStr, file);
                } else {
                    showToast("当前内容为空,请您触碰NFC设备，读取设备信息");
                }
            }
        });

        bt_uploading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upLoading();
            }
        });


    }

    private void saveXmlCacheDir(String xmlStr, File file) {
        try {
            NfcDataUtil.saveXml(xmlStr, file);
            showToast("保存成功");
            progressbar.setSecondaryProgress(100);
        } catch (Exception e) {
            e.printStackTrace();
            showToast("保存出错");
            progressbar.setSecondaryProgress(0);
        }
    }

    /**
     * 上传设备信息
     */
    private void upLoading() {

        final String uuid = ed_search.getText().toString().trim();
        final String xmlConfig = et_text_editor.getText().toString().trim();


        if (uuid.equals("")) {
            showToast("当前uuid不能为空");
            return;
        }
        myHandler.sendEmptyMessage(START_WRITE_NFC);

        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = MapHttpConfiguration.LOGIN_URl;

                RequestBody requestBody = new FormBody.Builder()
                        .add("username", "ld")
                        .add("password", "ld9102")
                        .build();


                HttpUtil.sendHttpRequest(url, new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        showToast("连接服务器失败！");
                        myHandler.sendEmptyMessage(STOP_WRITE_NFC);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String json = response.body().string();
                            myHandler.sendEmptyMessage(STOP_WRITE_NFC);

                            // 解析返回过来的json
                            Gson gson = new Gson();
                            LoginJson loginInfo = gson.fromJson(json, LoginJson.class);


                            if (loginInfo.getErrno() == 0) {

                                reportDevice(xmlConfig, uuid, loginInfo.getData().getToken().getToken());

                            } else {
                                showToast("连接服务器失败！");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showToast("获取异常错误 ：" + e.getMessage());
                        }

                    }
                }, requestBody);

            }
        }).start();


    }

    /**
     * 汇报设备信息到服务器
     *
     * @param xmlConfig xml 配置信息
     * @param uuid      上传保存的 uuid
     * @param token     登录的 token
     */
    private void reportDevice(final String xmlConfig, final String uuid, final String token) {


        new Thread(new Runnable() {
            @Override
            public void run() {

                // 格式化xml
                String xml = null;
                Document document = null;
                try {
                    document = DocumentHelper.parseText(xmlConfig);
                    xml = document.getRootElement().asXML(); //可以去掉头部内容
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                String url = MapHttpConfiguration.REPORT_CONFIG_URL;
                //     String postBody = "{\"UUID\":\"2016C0312000001200001192\",\"config\": {\"xml_config\": \"21351515615sdf1sd61fs651d65f465sd46f54s6d54f33998\"}}";
                String postBody = " {\"UUID\": \"" + uuid + "\",\"config\": {\"xml_config\":\"" + replaceBlank(xml) + "\"}}";


                RequestBody body = FormBody.create(MediaType.parse("application/json"), postBody);


                HttpUtil.sendHttpRequest(url, new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println("" + "失败" + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String json = response.body().string();
                        showToast("上传完成");

                    }
                }, token, body);
            }
        }).start();

    }

    /**
     * 清理界面
     */
    private void clearInterface() {
        et_text_editor.setText("");
        // 初始化进度条
        initProgressBar();
        // 删除编辑的缓存文件
        NfcDataUtil.deleFile(new File(NfcNdefActivity.this.getCacheDir(), NFC_EIDT_DATA_CACHE));
    }

    /**
     * 跳转到二维码扫描界面
     */
    private void goScan() {
        Intent intent = new Intent(NfcNdefActivity.this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_QR);
    }

    private void initView() {

        // 临时，初始化电参数据上传服务器
        initLight();

        //将Activity传入以便获取contentView
        AutoFitKeyBoardUtil.getInstance().assistActivity(this);

        ll = (LinearLayout) findViewById(R.id.ll_nfc);
        ed_search = (EditText) this.findViewById(R.id.ed_search);
        bt_save_config = (TextView) this.findViewById(R.id.bt_save_config);
        tv_deploy = (TextView) this.findViewById(R.id.tv_deploy);
        tv_write = (TextView) this.findViewById(R.id.tv_write);
        tv_edit_switch = (TextView) this.findViewById(R.id.tv_edit_switch);
        et_text_editor = (EditText) this.findViewById(R.id.et_text_editor);
        tb_nfc_switch = (ToggleButton) this.findViewById(R.id.tb_nfc_switch);
        bt_clear = (Button) this.findViewById(R.id.bt_clear);
        progressbar = (ProgressBar) this.findViewById(R.id.progressbar);
        bt_uploading = (Button) this.findViewById(R.id.bt_uploading);
        tv_location = (TextView) this.findViewById(R.id.tv_location);

        // 清除当前界面信息
        clearInterface();


        // 写入提示框
        View view = View.inflate(NfcNdefActivity.this, R.layout.alert_dialog_item, null);
        writeAlertDialog = new AlertDialog.Builder(NfcNdefActivity.this).setTitle("提示")
                .setView(view)
                .setCancelable(false)
                .setPositiveButton("写入", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                     /*   try {
                            Field field = dialog.getClass().getSuperclass()
                                    .getDeclaredField("mShowing");
                            field.setAccessible(true);
                            field.set(dialog, false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/


                        // 写入
                        //     writeNfc();


                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                      /*  try {
                            Field field = dialog.getClass().getSuperclass()
                                    .getDeclaredField("mShowing");
                            field.setAccessible(true);
                            field.set(dialog, true);
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/

                        //    showToast("已经取消");
                    }
                }).create();


        writeAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button btnPositive = writeAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                final ToggleButton tbLocation = writeAlertDialog.findViewById(tb_location);
                btnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 判断是否更新经纬度
                        if (tbLocation.isChecked()) {
                            // 更新经纬度信息
                            upLocation();

                        } else {
                            // 写入操作执行，先检测数据
                            checkWrite();
                        }

                    }
                });

                // 经纬度上传监听
                final EditText et_longitude = writeAlertDialog.findViewById(R.id.et_longitude);
                final EditText et_latitude = writeAlertDialog.findViewById(R.id.et_latitude);
                if (cAMapLocation != null) {
                    et_longitude.setText(cAMapLocation.getLongitude() + "");
                    et_latitude.setText(cAMapLocation.getLatitude() + "");

                }
                final LinearLayout ll_uplocation = writeAlertDialog.findViewById(R.id.ll_uplocation);
                tbLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tbLocation.isChecked() == true) {
                            if (cAMapLocation != null) {
                                et_longitude.setText(cAMapLocation.getLongitude() + "");
                                et_latitude.setText(cAMapLocation.getLatitude() + "");
                                ll_uplocation.setVisibility(View.VISIBLE);

                            } else {
                                tbLocation.setChecked(false);
                                ll_uplocation.setVisibility(View.GONE);
                                showToast("定位失败，无法获取经纬度信息");
                            }
                        } else {
                            ll_uplocation.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        iv_location = (ImageView) this.findViewById(R.id.iv_location);
        iv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NfcNdefActivity.this, AMapLocationActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ORIENTATION);

            }
        });
    }


    Button bt_nfc_light_submit;
    EditText et_device_info_UUID, et_device_info_name1, et_device_info_name2, et_device_info_name3, et_device_info_lat, et_device_info_lng, et_device_info_lamp_diameter,
            et_device_info_power_manufacturer, et_device_info_lamp_ratedCurrent, et_device_info_lamp_ratedvoltage, et_device_info_lampType, et_device_info_lamp_manufacturer,
            et_device_info_lamp_num, et_device_info_poleProductionDate, et_device_info_pole_height, et_device_info_rated_power, et_device_info_subcommunicate_mode;
    String currentUuid;
    Gson gson = new Gson();

    private void initLight() {

        et_device_info_UUID = (EditText) this.findViewById(R.id.et_device_info_UUID);
        et_device_info_name1 = (EditText) this.findViewById(R.id.et_device_info_name1);
        et_device_info_name2 = (EditText) this.findViewById(R.id.et_device_info_name2);
        et_device_info_name3 = (EditText) this.findViewById(R.id.et_device_info_name3);
        et_device_info_lat = (EditText) this.findViewById(R.id.et_device_info_lat);
        et_device_info_lng = (EditText) this.findViewById(R.id.et_device_info_lng);
        et_device_info_lamp_diameter = (EditText) this.findViewById(R.id.et_device_info_lamp_diameter);
        et_device_info_power_manufacturer = (EditText) this.findViewById(R.id.et_device_info_power_manufacturer);
        et_device_info_lamp_ratedCurrent = (EditText) this.findViewById(R.id.et_device_info_lamp_ratedCurrent);
        et_device_info_lamp_ratedvoltage = (EditText) this.findViewById(R.id.et_device_info_lamp_ratedvoltage);
        et_device_info_lampType = (EditText) this.findViewById(R.id.et_device_info_lampType);
        et_device_info_lamp_manufacturer = (EditText) this.findViewById(R.id.et_device_info_lamp_manufacturer);
        et_device_info_lamp_num = (EditText) this.findViewById(R.id.et_device_info_lamp_num);
        et_device_info_poleProductionDate = (EditText) this.findViewById(R.id.et_device_info_poleProductionDate);
        et_device_info_pole_height = (EditText) this.findViewById(R.id.et_device_info_pole_height);
        et_device_info_rated_power = (EditText) this.findViewById(R.id.et_device_info_rated_power);
        et_device_info_subcommunicate_mode = (EditText) this.findViewById(R.id.et_device_info_subcommunicate_mode);

        // 获取保存数据
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(NfcNdefActivity.this);
        String lampData = prefs.getString(Config.KEY_DEVICE_LAMP_DATA, "");
        if (lampData != "") {
            LampEditData lampEditData = gson.fromJson(lampData, LampEditData.class);
            et_device_info_UUID.setText(lampEditData.getUUID());

            //   et_device_info_UUID.setText("83140000862285036010878");

            et_device_info_name1.setText(lampEditData.getNAME1());
            et_device_info_name2.setText(lampEditData.getNAME2());
            et_device_info_name3.setText(lampEditData.getNAME3());
            et_device_info_lat.setText(lampEditData.getLAT());
            et_device_info_lng.setText(lampEditData.getLNG());
            et_device_info_lamp_diameter.setText(lampEditData.getLampDiameter());
            et_device_info_power_manufacturer.setText(lampEditData.getPower_Manufacturer());
            et_device_info_lamp_ratedCurrent.setText(lampEditData.getLamp_RatedCurrent());
            et_device_info_lamp_ratedvoltage.setText(lampEditData.getLamp_Ratedvoltage());
            et_device_info_lampType.setText(lampEditData.getLampType());
            et_device_info_lamp_manufacturer.setText(lampEditData.getLamp_Manufacturer());
            et_device_info_lamp_num.setText(lampEditData.getLamp_Num());
            et_device_info_poleProductionDate.setText(lampEditData.getPoleProductionDate());
            et_device_info_pole_height.setText(lampEditData.getPole_height());
            et_device_info_rated_power.setText(lampEditData.getRated_power());
            et_device_info_subcommunicate_mode.setText(lampEditData.getSubcommunicate_mode());
        }

        bt_nfc_light_submit = (Button) this.findViewById(R.id.bt_nfc_light_submit);
        bt_nfc_light_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // 保存数据
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(NfcNdefActivity.this);
                SharedPreferences.Editor editor = sharedPrefs.edit();

                LampEditData lampEditData = new LampEditData();
                lampEditData.setUUID(et_device_info_UUID.getText().toString());
                lampEditData.setNAME1(et_device_info_name1.getText().toString());
                lampEditData.setNAME2(et_device_info_name2.getText().toString());
                lampEditData.setNAME3(et_device_info_name3.getText().toString());
                lampEditData.setLAT(et_device_info_lat.getText().toString());
                lampEditData.setLNG(et_device_info_lng.getText().toString());
                lampEditData.setLampDiameter(et_device_info_lamp_diameter.getText().toString());
                lampEditData.setPower_Manufacturer(et_device_info_power_manufacturer.getText().toString());
                lampEditData.setLamp_RatedCurrent(et_device_info_lamp_ratedCurrent.getText().toString());
                lampEditData.setLamp_Ratedvoltage(et_device_info_lamp_ratedvoltage.getText().toString());
                lampEditData.setLampType(et_device_info_lampType.getText().toString());
                lampEditData.setLamp_Manufacturer(et_device_info_lamp_manufacturer.getText().toString());
                lampEditData.setLamp_Num(et_device_info_lamp_num.getText().toString());
                lampEditData.setPoleProductionDate(et_device_info_poleProductionDate.getText().toString());
                lampEditData.setPole_height(et_device_info_pole_height.getText().toString());
                lampEditData.setRated_power(et_device_info_rated_power.getText().toString());
                lampEditData.setSubcommunicate_mode(et_device_info_subcommunicate_mode.getText().toString());
                editor.putString(Config.KEY_DEVICE_LAMP_DATA, gson.toJson(lampEditData));
                editor.commit();


                //currentUuid = "83140000862285036010878";
                currentUuid = lampEditData.getUUID();
                if (currentUuid != null && !currentUuid.equals("")) {
                    showProgress();
                    // 获取 Dialog 中的经纬度
              /*      String longitude = ((EditText)writeAlertDialog.findViewById(R.id.et_longitude)).getText().toString();
                    String latitude = ((EditText)writeAlertDialog.findViewById(R.id.et_latitude)).getText().toString();*/


                    //    String url = "https://ludeng.stgxy.com:9443/api/device_lamp/edit";
                    String url = "https://iot.sz-luoding.com:2890/api/device_lamp/list";
                    // String postBody = "{\"data\":{ \"LNG\":"+"106.541652"+",\"\"LAT:" +"29.803828" +"},\"where\":{ \"UUID\":"+"000000000000000000000022" +"} }";

                    JSONStringer jsonstr = null;
                    try {
                        jsonstr = new JSONStringer()
                                .object().key("size").value(2000)
                                .key("where").object().key("UUID").value(currentUuid)
                                .endObject().endObject();
                        LogUtil.e("jsonstr = " + jsonstr.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //  String postBody = "{\"data\":{ \"LNG\":\"106.541654\",\"LAT\":\"29.803828\"},\"where\":{ \"UUID\":\"000000000000000000000022\"} }";
                    RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), jsonstr.toString());

                    HttpUtil.sendHttpRequest(url, new Callback() {

                        @Override
                        public void onFailure(Call call, IOException e) {
                            NfcNdefActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("连接服务器异常！");
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            NfcNdefActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                        String data = response.body().string();
                                        DeviceLampData deviceLampSingleData = gson.fromJson(data, DeviceLampData.class);
                                        if (deviceLampSingleData.getErrno() != 0) {
                                            showToast("服务器访问异常~");
                                            return;
                                        }

                                        //  https://iot2.sz-luoding.com:2888/api/device_lamp/edit?upsert=1&id=28
                                        LogUtil.e("data = " + data);
                                        String url = "https://iot.sz-luoding.com:2890/api/device_lamp/edit?upsert=1&id=" + deviceLampSingleData.getData().getData().get(0).get_id();
                                        JSONStringer jsonstr = null;
                                        try {

                                            LampEditData lampEditData = new LampEditData();
                                            lampEditData.setUUID(et_device_info_UUID.getText().toString());
                                            // lampEditData.setUUID("83140000862285036010878");

                                            lampEditData.setNAME1(et_device_info_name1.getText().toString());
                                            lampEditData.setNAME2(et_device_info_name2.getText().toString());
                                            lampEditData.setNAME3(et_device_info_name3.getText().toString());
                                            lampEditData.setLAT(et_device_info_lat.getText().toString());
                                            lampEditData.setLNG(et_device_info_lng.getText().toString());
                                            lampEditData.setLampDiameter(et_device_info_lamp_diameter.getText().toString());
                                            lampEditData.setPower_Manufacturer(et_device_info_power_manufacturer.getText().toString());
                                            lampEditData.setLamp_RatedCurrent(et_device_info_lamp_ratedCurrent.getText().toString());
                                            lampEditData.setLamp_Ratedvoltage(et_device_info_lamp_ratedvoltage.getText().toString());
                                            lampEditData.setLampType(et_device_info_lampType.getText().toString());
                                            lampEditData.setLamp_Manufacturer(et_device_info_lamp_manufacturer.getText().toString());
                                            lampEditData.setLamp_Num(et_device_info_lamp_num.getText().toString());
                                            lampEditData.setPoleProductionDate(et_device_info_poleProductionDate.getText().toString());
                                            lampEditData.setPole_height(et_device_info_pole_height.getText().toString());
                                            lampEditData.setRated_power(et_device_info_rated_power.getText().toString());
                                            lampEditData.setSubcommunicate_mode(et_device_info_subcommunicate_mode.getText().toString());

                                            jsonstr = new JSONStringer()
                                                    .object()
                                                    //  .key("LAT").value(lampEditData.getLAT())
                                                    // .key("LNG").value(lampEditData.getLNG())
                                                    .key("NAME").value(lampEditData.getNAME1() + lampEditData.getNAME2() + lampEditData.getNAME3())
                                                    .key("LampDiameter").value(lampEditData.getLampDiameter())
                                                    .key("Power_Manufacturer").value(lampEditData.getPower_Manufacturer())
                                                    .key("Lamp_RatedCurrent").value(lampEditData.getLamp_RatedCurrent())
                                                    .key("Lamp_Ratedvoltage").value(lampEditData.getLamp_Ratedvoltage())
                                                    .key("lampType").value(lampEditData.getLampType())
                                                    .key("Lamp_Manufacturer").value(lampEditData.getLamp_Manufacturer())
                                                    .key("Lamp_Num").value(lampEditData.getLamp_Num())
                                                    .key("PoleProductionDate").value(lampEditData.getPoleProductionDate())
                                                    .key("Pole_height").value(lampEditData.getPole_height())
                                                    .key("Rated_power").value(lampEditData.getRated_power())
                                                    .key("Subcommunicate_mode").value(lampEditData.getSubcommunicate_mode())
                                                    .endObject();
                                            LogUtil.e("jsonstr = " + jsonstr.toString());

                                            stopProgress();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        LogUtil.e("jsonstr = " + jsonstr.toString());
                                        //  String postBody = "{\"data\":{ \"LNG\":\"106.541654\",\"LAT\":\"29.803828\"},\"where\":{ \"UUID\":\"000000000000000000000022\"} }";
                                        RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), jsonstr.toString());
                                        HttpUtil.sendHttpRequest(url, new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {

                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                showToast("更新经纬度成功~");
                                                myHandler.sendEmptyMessage(STOP_WRITE_NFC);

                                            }
                                        }, token, requestBody);


                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
                    }, token, requestBody);

                } else {
                    stopProgress();
                    showToast("uuid 有误，请先读取uuid");
                }
            }
        });

    }

    /**
     * 经纬度更新
     */
    private void upLocation() {

        if (token == null) {
            getToken();
            showToast("当前 token 为空，请重新写入！");
        } else {
            try {
                httpUpLocation();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    String regionN, proN, imei, uuid;

    private void httpUpLocation() throws Exception {

        // 解析xml文件，获取url
        FileInputStream inputStream = new FileInputStream(new File(NfcNdefActivity.this.getCacheDir(), NFC_EIDT_DATA_CACHE));
        List<DataDictionaries> dataDictionaries = NfcDataUtil.parseXml2(inputStream);

        for (DataDictionaries dataDictionarie : dataDictionaries) {
            if (dataDictionarie.getName().equals("项目地区")) {
                regionN = dataDictionarie.getXmValue();
            } else if (dataDictionarie.getName().equals("项目编号")) {
                proN = dataDictionarie.getXmValue();
            } else if (dataDictionarie.getName().equals("IMEI")) {
                imei = dataDictionarie.getXmValue();
            }
        }
        uuid = regionN + proN + imei;
        if (uuid != null) {

            // 获取 Dialog 中的经纬度
            String longitude = ((EditText) writeAlertDialog.findViewById(R.id.et_longitude)).getText().toString();
            String latitude = ((EditText) writeAlertDialog.findViewById(R.id.et_latitude)).getText().toString();


            String url = "https://ludeng.stgxy.com:9443/api/device_lamp/edit";
            // String postBody = "{\"data\":{ \"LNG\":"+"106.541652"+",\"\"LAT:" +"29.803828" +"},\"where\":{ \"UUID\":"+"000000000000000000000022" +"} }";
            JSONStringer jsonstr = new JSONStringer()
                    .object().key("data")
                    .object().key("LNG").value(longitude)
                    .key("LAT").value(latitude)
                    .endObject()
                    .key("where").object().key("UUID").value(uuid)
                    .endObject().endObject();
            //  String postBody = "{\"data\":{ \"LNG\":\"106.541654\",\"LAT\":\"29.803828\"},\"where\":{ \"UUID\":\"000000000000000000000022\"} }";
            RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), jsonstr.toString());

            HttpUtil.sendHttpRequest(url, new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    NfcNdefActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("连接服务器异常！");
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    NfcNdefActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                showToast("更新经纬度成功" + response.body().string());

                                checkWrite();

                                myHandler.sendEmptyMessage(STOP_WRITE_NFC);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }, token, requestBody);

        } else {
            showToast("uuid 有误，请先读取uuid");
        }

    }

    private void getToken() {

       /* new Thread(new Runnable() {
            @Override
            public void run() {

                //   String url = "https://ludeng.stgxy.com:9443/api/user/login";
                String url = "https://ludeng.stgxy.com:9443/api/user/login";
              *//*  RequestBody requestBody = new FormBody.Builder()
                        .add("strTemplate", "{\"ischeck\":$data.rows}")
                        .add("username", "cy")
                        .add("password", "@@ld9102")
                        .add("strVerify", "[admin]")
                        .build();*//*

              *//*  RequestBody requestBody = new FormBody.Builder()
                        .add("strTemplate", "{\"ischeck\":$data.rows}")
                        .add("username", "admin")
                        .add("password", "Ld@cc0unt")
                        .add("strVerify", "[admin]")
                        .build();*//*

                RequestBody requestBody = new FormBody.Builder()
                        .add("strTemplate", "{\"ischeck\":$data.rows}")
                        .add("username", "cy")
                        .add("password", "@@ld9102")
                        .add("strVerify", "[admin]")
                        .build();


                HttpUtil.sendHttpRequest(url, new Callback() {


                    @Override
                    public void onFailure(Call call, IOException e) {
                        showToast("连接服务器异常！");
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {


                        String json = response.body().string();
                        Gson gson = new Gson();
                        LoginInfo loginInfo = gson.fromJson(json, LoginInfo.class);
                        if (loginInfo.getErrno() == 0) {
                            token = loginInfo.getData().getToken().getToken();
                        }

                    }
                }, requestBody);
            }
        }).start();*/

        new Thread(new Runnable() {
            @Override
            public void run() {

                //   String url = "https://ludeng.stgxy.com:9443/api/user/login";
                // String url = "https://iot2.sz-luoding.com:2888/api/user/login";
                // String url = "https://iot.sz-luoding.com:888/api/user/login";
                String url = "https://iot.sz-luoding.com:2890/api/user/login";



              /*  RequestBody requestBody = new FormBody.Builder()
                        .add("strTemplate", "{\"ischeck\":$data.rows}")
                        .add("username", "cy")
                        .add("password", "@@ld9102")
                        .add("strVerify", "[admin]")
                        .build();*/

              /*  RequestBody requestBody = new FormBody.Builder()
                        .add("strTemplate", "{\"ischeck\":$data.rows}")
                        .add("username", "admin")
                        .add("password", "Ld@cc0unt")
                        .add("strVerify", "[admin]")
                        .build();*/

                RequestBody requestBody = new FormBody.Builder()
                        .add("strTemplate", "{\"ischeck\":$data.rows}")
                        .add("username", "ld")
                        .add("password", "ld9102")
                        .add("strVerify", "[admin]")
                        .build();


                HttpUtil.sendHttpRequest(url, new Callback() {


                    @Override
                    public void onFailure(Call call, IOException e) {
                        showToast("连接服务器异常！");
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {


                        String json = response.body().string();
                        Gson gson = new Gson();
                        LoginInfo loginInfo = gson.fromJson(json, LoginInfo.class);
                        if (loginInfo.getErrno() == 0) {
                            token = loginInfo.getData().getToken().getToken();
                        }

                    }
                }, requestBody);
            }
        }).start();

    }

    private void checkWrite() {

        try {

            // 解析xml文件，得到所有参数

         /*   FileInputStream inputStream = new FileInputStream(new File(NfcNdefActivity.this.getCacheDir(), NFC_EIDT_DATA_CACHE));
            List<DataDictionaries> dataDictionaries = NfcDataUtil.parseXml2(inputStream);*/

           /* InputStream is = NfcNdefActivity.this.getAssets().open("xml/NfcDataCache.xml");
            FileInputStream inputStream = (FileInputStream) (is);
            List<DataDictionaries> dataDictionaries = NfcDataUtil.parseXml2(inputStream);*/


            FileInputStream inputStream;
            List<DataDictionaries> dataDictionaries;
            final ToggleButton tbDefault = writeAlertDialog.findViewById(R.id.tb_default);
            if (tbDefault.isChecked()) {
                File file = new File(NfcNdefActivity.this.getCacheDir(), "moren.xml");
                if (!file.exists()) {
                    saveXmlCacheDir(cxml, file);
                }
                inputStream = new FileInputStream(new File(NfcNdefActivity.this.getCacheDir(), "moren.xml"));
                dataDictionaries = NfcDataUtil.parseXml2(inputStream);
            } else {
                inputStream = new FileInputStream(new File(NfcNdefActivity.this.getCacheDir(), NFC_EIDT_DATA_CACHE));
                dataDictionaries = NfcDataUtil.parseXml2(inputStream);
            }


            // 如果设备类型为 1 时，需要匹配 IMEI 码
         /*   for (DataDictionaries dataDictionarie : dataDictionaries) {
                if (dataDictionarie.getName().equals("设备类型")) {
                    if (dataDictionarie.getXmValue().equals("0001")) {
                        // 获 MEI 码
                        List<String> imeis = NfcDataUtil.parseImeiExcel("IMEI/v_device_lamp.xls", NfcNdefActivity.this);

                        DataDictionaries cImei = null;
                        boolean imeiVerify = false;
                        for (DataDictionaries imei : dataDictionaries) {
                            if (imei.getName().equals("IMEI")) {
                                cImei = imei;
                                break;
                            }
                        }
                        if (cImei == null) {

                            return;
                        } else {
                            for (int i = 0; i < imeis.size(); i++) {
                                if (imeis.get(i).contains(cImei.getXmValue())) {
                                    imeiVerify = true;
                                    System.out.println(">>>>>>>>>>>>>>>>> imeiVerify 成功 ");
                                    break;

                                }
                            }
                        }
                        if (!imeiVerify) {
                            showToast("IMEI 验证失败！");
                            return;
                        }
                    }
                }
            }*/

            // 写入
            writeNfc(dataDictionaries);

        } catch (Exception e) {
            e.printStackTrace();
            showToast("校验 IMEI 异常， 写入失败");
            return;
        }
    }

    private void writeNfc(final List<DataDictionaries> dataDictionaries) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    // 通知 Handle 当前正在写入nfc
                    myHandler.sendEmptyMessage(START_WRITE_NFC);

   /*                    for (int i = 0; i < dataDictionaries.size(); i++) {
                        System.out.println("xxxx = "+dataDictionaries.get(i).getStartAddress() + " - " + dataDictionaries.get(i).getEndAddress() + "  " + dataDictionaries.get(i).getName() + Arrays.toString(dataDictionaries.get(i).getValue() ) + ""  + dataDictionaries.get(i).getXmValue());
                    }*/


                    // 解析excel
                    // 校验获取的参数是否符合规定,然后写入
                    NfcDataUtil.writeNfcDeviceInfo2(dataDictionaries, new NfcDataUtil.OnNfcDataListening() {
                        @Override
                        public void succeed() {
                            showToast("写入成功");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressbar.setProgress(100);
                                    //  通知 Handle nfc 已关闭写入
                                    myHandler.sendEmptyMessage(STOP_WRITE_NFC);
                                }
                            });
                        }

                        @Override
                        public void failure(String error) {
                            showToast(error);
                            //  通知 Handle nfc 已关闭写入
                            progressbar.setProgress(0);
                            myHandler.sendEmptyMessage(STOP_WRITE_NFC);
                        }

                    }, NfcNdefActivity.this, mTag, payload);


                } catch (Exception e) {
                    e.printStackTrace();
                    // 初始化进度条
                    initProgressBar();
                    //  通知 Handle nfc 已关闭写入
                    myHandler.sendEmptyMessage(STOP_WRITE_NFC);

                    showToast("" + e.getMessage().toString());
                }

            }
        }).start();


    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

       /* // 处理二维码扫描结果
        if (requestCode == REQUEST_CODE_ZXING) {
            //处理扫描结果（在界面上显示）
            if (null != intent) {
                Bundle bundle = intent.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    ed_search.setText(result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    showToast("解析二维码失败");
                }
            }
        }*/


        switch (requestCode) {
            case REQUEST_CODE_QR:// 二维码
                // 扫描二维码回传
                if (resultCode == RESULT_OK) {
                    if (intent != null) {
                        //获取扫描结果
                        Bundle bundle = intent.getExtras();
                        String result = bundle.getString(CaptureActivity.EXTRA_STRING);
                        ed_search.setText(result);
                    }
                }
                break;
            case REQUEST_CODE_ORIENTATION:// 定位返回
                // 定位返回
                if (resultCode == RESULT_OK) {
                    if (intent != null) {
                        LatLng latLng = intent.getParcelableExtra("location");
                        if (latLng != null) {
                            et_device_info_lat.setText(latLng.latitude + "");
                            et_device_info_lng.setText(latLng.longitude + "");
                        }
                    }
                }
                break;
            default:
                break;
        }
    }


    /**
     * 初始化进度条
     */
    private void initProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressbar != null) {
                    progressbar.setProgress(0);
                    progressbar.setSecondaryProgress(0);
                }
            }
        });

    }


    protected void onPostExecute(byte[] mBuffer) {

        System.out.println("xxxxxxxxx " + Arrays.toString(mBuffer));
        if (mBuffer != null) {

            // 判断nfc硬件类型
            byte[] typeByte = new byte[2];
            //   System.arraycopy(mBuffer, 3+ 29, typeByte, 0, 2);
            System.arraycopy(mBuffer, 29, typeByte, 0, 2);
                /*typeByte[0] = 0;
                typeByte[1] = 3;*/
            System.out.println("xxxxxxxxx " + Arrays.toString(typeByte) + "  " + BytesUtil.bytesIntHL(typeByte));
            if (BytesUtil.bytesIntHL(typeByte) == 1) {
                // 根据类型读取 nfc
                readNfcByType("0001_83140000.xls", mBuffer);
            } else if (BytesUtil.bytesIntHL(typeByte) == 2) {
                // 根据类型读取 nfc
                readNfcByType("0002_83140000.xls", mBuffer);
            } else if (BytesUtil.bytesIntHL(typeByte) == 3) {
                // 根据类型读取 nfc
                readNfcByType("0003_83140000.xls", mBuffer);
            } else {
                showToast("当前类型无法解析");
            }
        }
    }

    /**
     * 根据 nfc 的标识类型读取内容信息
     *
     * @param nfcFileName 类型对应的 nfc 文件名
     */
    private void readNfcByType(String nfcFileName, byte[] mBuffer) {


        LogUtil.e("nfcFileName = " + nfcFileName);
        // 解析成xml文件
        File cacheFile = NfcDataUtil.parseBytesToXml(mBuffer, nfcFileName, NFC_DATA_CACHE, NfcNdefActivity.this);

        if (cacheFile != null) {
            try {

                LogUtil.e("writeAlertDialog.isShowing() = " + writeAlertDialog.isShowing());

                // 更新 Dialog
                if (writeAlertDialog.isShowing()) {
                    // 解析xml文件，得到所有参数
                    FileInputStream inputStream = new FileInputStream(cacheFile);
                    List<DataDictionaries> dataDictionaries = NfcDataUtil.parseXml2(inputStream);
                    String baseplateId = null;
                    for (int i = 0; i < dataDictionaries.size(); i++) {
                        if (dataDictionaries.get(i).getName().equals("执行底板ID")) {
                            baseplateId = dataDictionaries.get(i).getXmValue();
                            break;
                        }
                    }

                    // 通过 Handle 更新 AlertDialog
                    Message tempMsg = myHandler.obtainMessage();
                    tempMsg.what = HANDLE_UP_WRITE;
                    tempMsg.obj = baseplateId.replaceAll(" +", "");
                    myHandler.sendMessage(tempMsg);
                }

                if (et_text_editor.getText().toString().equals("")) {
                    //读取文件流
                    FileInputStream fis = new FileInputStream(cacheFile);
                    int size = fis.available();
                    System.out.println("可读取的字节数 " + size);
                    byte[] buffer = new byte[size];
                    fis.read(buffer);
                    String txt = new String(buffer, 0, buffer.length);
                    //  LogUtil.e("xxx XmlUtil.formatXml(txt) =" + NfcDataUtil.formatXml(txt));
                    et_text_editor.setText(NfcDataUtil.formatXml(txt));
                    // 初始化进度条
                    initProgressBar();
                    fis.close();
                }

                // 获取uuid
                for (int i = 0; i < dataDictionaries.size(); i++) {
                    if (dataDictionaries.get(i).getName().equals("项目地区")) {
                        regionN = dataDictionaries.get(i).getXmValue();
                    } else if (dataDictionaries.get(i).getName().equals("项目编号")) {
                        proN = dataDictionaries.get(i).getXmValue();
                    } else if (dataDictionaries.get(i).getName().equals("IMEI")) {
                        imei = dataDictionaries.get(i).getXmValue();
                    }
                }
                currentUuid = regionN + proN + imei;
                et_device_info_UUID.setText(currentUuid);

                int num = Integer.parseInt(et_device_info_name2.getText().toString());
                et_device_info_name2.setText(++num + ""); // 灯杆号自加


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(NfcNdefActivity.this, "读取失败！", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onNewIntent(Intent intent) {

        //获取Tag对象
        mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        readNfcTag(intent);

   /*     Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String action = intent.getAction();
        Ndef ndef = Ndef.get(detectedTag);
        if (ndef == null) {
            // NDEF is not supported by this Tag.
            return;
        }
        NdefMessage ndefMessage = ndef.getCachedNdefMessage();


        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
            //read each record
            byte[] da = ndefRecord.getPayload();
            System.out.println("xxxxxxxx data = " + Arrays.toString(da));
        }*/


      /*  mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Ndef ndef = Ndef.get(mTag);
        NdefMessage ndefMessage = ndef.getCachedNdefMessage();
        Parcelable[] rawMessage = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            System.out.println("rawMessage == null " + (rawMessage == null));*/



       /* try {
            mTag   = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Nfc nfcA = NfcA.get(mTag);
            nfcA.connect();
            byte[] SELECT = {(byte) 0x30, (byte) 0x05};//我读取的NFC卡片使用的是NTAG216的芯片，这里的指令参数是根据其datasheet的说明写的。
            byte[] result = nfcA.transceive(SELECT);//这
            System.out.println("tag A = " + Arrays.toString(result));
        } catch (IOException e) {
            e.printStackTrace();
        }*/



    /*    if (mTag!= null) {
            //Toast.makeText(this, "Reading Tag Info", Toast.LENGTH_SHORT).show();
            // get NDEF tag details
            Ndef ndefTag = Ndef.get(mTag);


           boolean  tag_writable = ndefTag.isWritable(); // is tag writable?

            // get NDEF message details
            NdefMessage ndefMesg = ndefTag.getCachedNdefMessage();
            NdefRecord[] ndefRecords = ndefMesg.getRecords();
            int len = ndefRecords.length;
           String[]  recTypes = new String[len];     // will contain the NDEF record types
            for (int i = 0; i < len; i++) {
                recTypes[i] = new String(ndefRecords[i].getType());
            }

            System.out.println("xxxxxxxxxx " + ndefTag.getMaxSize() + "  " + ndefTag.getType() + "  " + Arrays.toString(recTypes));
        }*/


    }


    private byte[] payload;

    private void readNfcTag(Intent intent) {

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            //   Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            Log.d("textRecord", "长度 = " + rawMsgs.length);
            NdefMessage msgs[] = null;
            int contentSize = 0;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                    contentSize += msgs[i].toByteArray().length;
                }
            }
            try {
                if (msgs != null) {
                    NdefRecord record = msgs[0].getRecords()[0];
                    payload = record.getPayload();

                    //   System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxx " + Arrays.toString(payload2));

                    //    et_text_editor.setText(Arrays.toString(payload));

                    // 获得数据
                    byte[] langBytes = Locale.CHINA.getLanguage().getBytes(Charset.forName("US-ASCII"));
                    int headLong = 1 + langBytes.length;
                    byte[] data = new byte[payload.length - headLong];
                    System.arraycopy(payload, headLong, data, 0, payload.length - headLong);


                    onPostExecute(data);


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mNfcAdapter != null) {
            Log.v(TAG, "disableForegroundDispatch");
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public void onResume() {
        Intent intent = getIntent();
        Log.d(TAG, "Resume mainActivity intent: " + intent);
        super.onResume();


        if (mNfcAdapter != null) {
            Log.v(TAG, "enableForegroundDispatch");
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null /*nfcFiltersArray*/, null /*nfcTechLists*/);

            if (mNfcAdapter.isEnabled()) {
                // NFC enabled
                tb_nfc_switch.setChecked(true);
            } else {
                // NFC disabled
                tb_nfc_switch.setChecked(false);
            }

        } else {
            // NFC not available on this phone!!!
            //  showToast(getString(R.string.nfc_not_available));
        }

        //    readNfc();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //移除布局监听
        AutoFitKeyBoardUtil.getInstance().onDestory();
        // 销毁定位
        destroyLocation();

    }

    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }
}
