package com.ldgd.ld_nfc_module.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.ldgd.ld_nfc_module.R;
import com.ldgd.ld_nfc_module.base.BaseActivity;
import com.ldgd.ld_nfc_module.crc.CRC16;
import com.ldgd.ld_nfc_module.entity.NfcDeviceInfo;
import com.ldgd.ld_nfc_module.json.LoginJson;
import com.ldgd.ld_nfc_module.util.AutoFitKeyBoardUtil;
import com.ldgd.ld_nfc_module.util.BytesUtil;
import com.ldgd.ld_nfc_module.util.DrawableUtil;
import com.ldgd.ld_nfc_module.util.HttpUtil;
import com.ldgd.ld_nfc_module.util.LogUtil;
import com.ldgd.ld_nfc_module.util.MapHttpConfiguration;
import com.ldgd.ld_nfc_module.util.NfcDataUtil;
import com.ldgd.ld_nfc_module.util.NfcUtils;
import com.ldgd.ld_nfc_module.util.TagDiscovery;
import com.ldgd.ld_nfc_module.zbar.CaptureActivity;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.TagHelper;
import com.st.st25sdk.type5.Type5Tag;
import com.st.st25sdk.type5.st25dv.ST25DVTag;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.ldgd.ld_nfc_module.util.NfcDataUtil.replaceBlank;
import static com.st.st25sdk.MultiAreaInterface.AREA1;

public class NFCActivity extends BaseActivity implements TagDiscovery.onTagDiscoveryCompletedListener, View.OnClickListener {

    private static final String TAG = "NFCActivity";
    public static final int REQUEST_CODE_QR = 10;
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
    static private ST25DVTag mTag;

    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private ContentViewAsync contentView;
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


    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case HANDLE_UP_WRITE:

                    LogUtil.e("xxx HANDLE_UP_WRITE");
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
                if (ContextCompat.checkSelfPermission(NFCActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NFCActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
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


                File file = new File(NFCActivity.this.getCacheDir(), NFC_EIDT_DATA_CACHE);
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
                        FileInputStream inputStream = new FileInputStream(new File(NFCActivity.this.getCacheDir(), NFC_EIDT_DATA_CACHE));
                        NfcDeviceInfo nfcDeviceInfo = NfcDataUtil.parseXml(inputStream);

                        // 通过 Handle 更新 AlertDialog
                        Message tempMsg = myHandler.obtainMessage();
                        tempMsg.what = HANDLE_UP_READ;
                        tempMsg.obj = nfcDeviceInfo.getBaseplateId().replaceAll(" +", "");
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
                NfcUtils.IsToSet(NFCActivity.this, tb_nfc_switch);
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
                    File file = new File(NFCActivity.this.getCacheDir(), NFC_EIDT_DATA_CACHE);
                    try {
                        NfcDataUtil.saveXml(xmlStr, file);
                        showToast("保存成功");
                        progressbar.setSecondaryProgress(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast("保存出错");
                        progressbar.setSecondaryProgress(0);
                    }
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
        showProgress();

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
                    public void onFailure(okhttp3.Call call, IOException e) {
                        showToast("连接服务器失败！");
                        stopProgress();
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, Response response) throws IOException {
                        try {
                            String json = response.body().string();
                            stopProgress();

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
        NfcDataUtil.deleFile(new File(NFCActivity.this.getCacheDir(), NFC_EIDT_DATA_CACHE));
    }

    /**
     * 跳转到二维码扫描界面
     */
    private void goScan() {
        Intent intent = new Intent(NFCActivity.this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_QR);
    }

    private void initView() {

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

        // 清除当前界面信息
        clearInterface();


        // 写入提示框
        View view = View.inflate(NFCActivity.this, R.layout.alert_dialog_item, null);
        writeAlertDialog = new AlertDialog.Builder(NFCActivity.this).setTitle("提示")
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

                        showToast("已经取消");
                    }
                }).create();


        writeAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button btnPositive = writeAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                btnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 写入
                        writeNfc();
                    }
                });
            }
        });


    }

    private void writeNfc() {


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 通知 Handle 当前正在写入nfc
                    myHandler.sendEmptyMessage(START_WRITE_NFC);

                    // 解析xml文件，得到所有参数
                    FileInputStream inputStream = new FileInputStream(new File(NFCActivity.this.getCacheDir(), NFC_EIDT_DATA_CACHE));
                    NfcDeviceInfo nfcDeviceInfo = NfcDataUtil.parseXml(inputStream);
                    // 校验获取的参数是否符合规定,然后写入
                    NfcDataUtil.writeNfcDeviceInfo(nfcDeviceInfo, new NfcDataUtil.OnNfcDataListening() {
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
                            myHandler.sendEmptyMessage(STOP_WRITE_NFC);
                        }


                    }, NFCActivity.this, mTag);
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
            default:
                break;
        }
    }

    /**
     * 读取的开始位置和结束位置
     */
    private int mStartAddress;
    private int mNumberOfBytes;

    private void readNfc() {

        if (mTag != null) {
            //设置为读取所有
            mStartAddress = 0;
            mNumberOfBytes = 508;
            //   mNumberOfBytes = 508;
            ReadTheBytes(mStartAddress, mNumberOfBytes);
        } else {
            showToast("标签不在场区内");
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

    /**
     * 从 mNumberOfBytes
     *
     * @param mStartAddress
     * @param mNumberOfBytes
     */
    private void ReadTheBytes(int mStartAddress, int mNumberOfBytes) {


      /*  Snackbar snackbar = Snackbar.make(v, "", Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.reading_x_bytes_starting_y_address, mStartAddress, mNumberOfBytes), this);
        snackbar.setActionTextColor(getResources().getColor(R.color.white));
        snackbar.show();*/

        // by defaut - read first area
        if (mTag instanceof Type5Tag) {
            startType5ReadingAndDisplaying(mStartAddress, mNumberOfBytes);
        } else startType4ReadingAndDisplaying(mTag, AREA1);
    }

    private void startType4ReadingAndDisplaying(NFCTag tag, int area) {
        contentView = new ContentViewAsync(tag, area);
        contentView.execute();
    }

    private void startType5ReadingAndDisplaying(int startAddress, int numberOfBytes) {

        mStartAddress = startAddress;
        mNumberOfBytes = numberOfBytes;
        contentView = new ContentViewAsync(mTag);
        contentView.execute();
    }


    /**
     * 异步读取NFC数组
     */
    class ContentViewAsync extends AsyncTask<Void, Integer, Boolean> {
        byte mBuffer[] = null;
        NFCTag mTag;
        int mArea;

        public ContentViewAsync(NFCTag myTag) {
            mTag = myTag;
        }

        public ContentViewAsync(NFCTag myTag, int myArea) {
            mTag = myTag;
            mArea = myArea;
        }

        public ContentViewAsync(byte[] buffer) {
            mBuffer = buffer;
        }

        protected Boolean doInBackground(Void... arg0) {
            if (mBuffer == null) {
                try {
                    // Type 5
                    mBuffer = mTag.readBytes(mStartAddress, mNumberOfBytes);
                    // Warning: readBytes() may return less bytes than requested
                    //调用publishProgress公布进度,最后onProgressUpdate方法将被执行
                    //   publishProgress((int) ((count / (float) total) * 100));
                    int nbrOfBytesRead = 0;
                    if (mBuffer != null) {
                        nbrOfBytesRead = mBuffer.length;
                        // 保存nfc读取的数据
                          /*  CacheUtils.putString(ReadFragmentActivity.this,"nfcdata",Arrays.toString(mBuffer));
                            showToast(R.string.save_read_data);*/

                    }
                    if (nbrOfBytesRead != mNumberOfBytes) {
                        showToast(R.string.error_during_read_operation, nbrOfBytesRead);
                    }
                } catch (STException e) {
                    Log.e(TAG, e.getMessage());
                    // showToast(R.string.Command_failed);
                    showToast("读取失败，请您靠近NFC设备");
                    return false;
                }

            } else {
                // buffer already initialized by constructor - no need to read Tag.
                // Nothing to do
            }
            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (mBuffer != null) {
                /*mAdapter = new CustomListAdapter(mBuffer);
                lv = (ListView) findViewById(R.id.readBlocksListView);
                lv.setAdapter(mAdapter);*/

                //    LogUtil.e("xxx onPostExecute mBuffer = " + Arrays.toString(mBuffer));

                // 判断nfc硬件类型
                byte[] typeByte = new byte[2];
                System.arraycopy(mBuffer, 0, typeByte, 0, 2);
                if (BytesUtil.bytesIntHL(typeByte) == 1) {

                    // 根据类型读取 nfc
                    readNfcByType("0001_83140000.xls");

                } else if (BytesUtil.bytesIntHL(typeByte) == 2) {

                    // 根据类型读取 nfc
                    readNfcByType("0002_83140000.xls");

                } else {
                    showToast("当前类型无法解析");
                }


            }
        }

        /**
         *  根据 nfc 的标识类型读取内容信息
         * @param nfcFileName 类型对应的 nfc 文件名
         */
        private void readNfcByType(String nfcFileName) {

            // 验证 crc
            byte[] getCrc = Arrays.copyOfRange(mBuffer, 5, 148);
           int crc1 = CRC16.calcCrc16(getCrc);
            byte[] getCrc2 = Arrays.copyOfRange(mBuffer, 3, 5);
            int crc2 = BytesUtil.bytesIntHL(getCrc2);
            if(crc1 == crc2){
                showToast("当前 CRC 验证为 true" );
            }else{
                showToast("当前 CRC 验证为 false");
            }

            // 解析成xml文件
            File cacheFile = NfcDataUtil.parseBytesToXml(mBuffer, nfcFileName, NFC_DATA_CACHE, NFCActivity.this);

            if (cacheFile != null) {
                try {

                    LogUtil.e("writeAlertDialog.isShowing() = " + writeAlertDialog.isShowing());

                    // 更新 Dialog
                    if (writeAlertDialog.isShowing()) {
                        // 解析xml文件，得到所有参数
                        FileInputStream inputStream = new FileInputStream(cacheFile);
                        NfcDeviceInfo nfcDeviceInfo = NfcDataUtil.parseXml(inputStream);
                        // 通过 Handle 更新 AlertDialog
                        Message tempMsg = myHandler.obtainMessage();
                        tempMsg.what = HANDLE_UP_WRITE;
                        tempMsg.obj = nfcDeviceInfo.getBaseplateId().replaceAll(" +", "");
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

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(NFCActivity.this, "读取失败！", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        private void snackBarUiThread() {
            runOnUiThread(new Thread(new Runnable() {
                public void run() {
                    // inform user that a read will be performed
                    Snackbar snackbar = Snackbar.make(ll, "", Snackbar.LENGTH_LONG);
                    snackbar.setText(getString(R.string.reading_x_bytes_starting_y_address, mStartAddress, mNumberOfBytes));
                    snackbar.setActionTextColor(getResources().getColor(R.color.white));
                    snackbar.show();
                }
            }));
        }
    }


    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        Log.e(TAG, "onNewIntent " + intent);
        setIntent(intent);

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

        processIntent(intent);

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

    private static NfcIntentHook mNfcIntentHook;

    @Override
    public void onClick(View v) {

    }

    public interface NfcIntentHook {
        void newNfcIntent(Intent intent);

    }

    void processIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        Log.e(TAG, "xxx processIntent " + intent);

        if (mNfcIntentHook != null) {
            // NFC Intent hook used only for test purpose!
            mNfcIntentHook.newNfcIntent(intent);
            return;
        }

        Tag androidTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (androidTag != null) {
            // A tag has been taped

            // Perform tag discovery in an asynchronous task
            // onTagDiscoveryCompleted() will be called when the discovery is completed.
            new TagDiscovery(this).execute(androidTag);

            // This intent has been processed. Reset it to be sure that we don't process it again
            // if the MainActivity is resumed
            setIntent(null);
        }
    }


    @Override
    public void onTagDiscoveryCompleted(NFCTag nfcTag, TagHelper.ProductID productId, STException e) {
        if (e != null) {
            Log.i(TAG, e.toString());
            Toast.makeText(getApplication(), R.string.error_while_reading_the_tag, Toast.LENGTH_LONG).show();
            return;
        }

        mTag = (ST25DVTag) nfcTag;
        Log.e("xxx productId =", "productId = " + productId);
        switch (productId) {
            case PRODUCT_ST_ST25DV64K_I:
            case PRODUCT_ST_ST25DV64K_J:
            case PRODUCT_ST_ST25DV16K_I:
            case PRODUCT_ST_ST25DV16K_J:
            case PRODUCT_ST_ST25DV04K_I:
            case PRODUCT_ST_ST25DV04K_J:
             /*   checkMailboxActivation();
                startTagActivity(ST25DVActivity.class, R.string.st25dv_menus);*/

                //   showToast("NFC 识别成功");

                LogUtil.e("writeAlertDialog.isShowing() = " + writeAlertDialog.isShowing());

                String str = et_text_editor.getText().toString().trim();
                if (str.equals("") || writeAlertDialog.isShowing()) {
                    readNfc();
                }
                break;


            default:
                Toast.makeText(getApplication(), getResources().getString(R.string.unknown_tag), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Product not recognized");
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //扫码
                    goScan();
                } else {
                    Toast.makeText(this, "你拒绝了权限申请，无法打开相机扫码哟！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //移除布局监听
        AutoFitKeyBoardUtil.getInstance().onDestory();
    }
}
