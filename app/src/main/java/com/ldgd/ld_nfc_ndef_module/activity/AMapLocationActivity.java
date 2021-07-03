package com.ldgd.ld_nfc_ndef_module.activity;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.ldgd.ld_nfc_ndef_module.R;
import com.ldgd.ld_nfc_ndef_module.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class AMapLocationActivity extends AppCompatActivity implements AMap.OnCameraChangeListener, AMap.OnMapClickListener, AMap.OnMapTouchListener {
    public static final String USER_INFO = "USER_INFO";
    private List<String> list = new ArrayList<String>();

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //  当前定位位置
    private AMapLocation mAMapLocation = null;
    private AlertDialog alarmDialog;

    private MapView mMapView = null;
    private AMap mAMap = null;
    private UiSettings mUiSettings;
    private Marker marker = null;
    private Marker moveMarker = null;
    private MarkerOptions markerOption;
    private MarkerOptions moveMarkerOption;
    // 四个按钮
    private LinearLayout llUserInfo, ll_alarm, ll_call, ll_cancel_alarm;
    // 用户拖动地图后，不再跟随移动，需要跟随移动时再把这个改成true
    private boolean followMove = true;
    // 移动或者定位所获得的地址
    private String addressName;
    private ImageView iv_reposition;
    private TextView tvLocation;
    private Button bt_location_return;
    private LatLng currentLatLng = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();

            //设置修改状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            //设置状态栏的颜色，和你的app主题或者标题栏颜色设置一致就ok了
            window.setStatusBarColor(getResources().getColor(R.color.colorGreen));

            if (actionBar != null) {
                actionBar.hide();
            }
        }*/
        setContentView(R.layout.activity_main);


        initView(savedInstanceState);


        // 初始化地图
        initMap();

        // 请求权限
        requestPermission();

        // 初始化定位
        initLocation();



    }

    private void initView(Bundle savedInstanceState) {
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

       tvLocation = (TextView) this.findViewById(R.id.tv_location);
       /* RelativeLayout rl_location= (RelativeLayout) findViewById(R.id.rl_location);//布局
        rl_location.setBackgroundColor(Color.TRANSPARENT);*/

        bt_location_return = (Button) this.findViewById(R.id.bt_location_return);
        bt_location_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.putExtra("location",currentLatLng);
                //通过Intent对象返回结果，使setResult
                setResult(RESULT_OK,intent);
                //结束当前的Activity生命周期
                finish();

            }
        });


    }



    private String randomCode() {
        String strRand = "";
        for (int i = 0; i < 2; i++) {
            strRand += String.valueOf((int) (Math.random() * 10));
        }
        return strRand;
    }

    private void showToast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(AMapLocationActivity.this, str, Toast.LENGTH_SHORT);
                toast.setText(str);
                toast.show();

            }
        });

    }

    protected ProgressDialog mProgress;

    protected void showProgress() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress = ProgressDialog.show(AMapLocationActivity.this, "", "请稍等...");
            }
        });

    }

    private void stopProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress.cancel();
            }
        });
    }

    private void initMap() {
        if (mAMap == null) {
            // 初始化地图
            mAMap = mMapView.getMap();
            mUiSettings = mAMap.getUiSettings();
            //  mAMap.setOnMapLoadedListener(this);
            // 设置地图样式
            //  setMapCustomStyleFile(this);
            // 设置地图logo显示在右下方
            mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
            // 设置地图默认的缩放按钮是否显示
            mUiSettings.setZoomControlsEnabled(false);
            // 设置地图缩放比例
            mAMap.moveCamera(CameraUpdateFactory.zoomTo(5f));
            //  对amap添加移动地图事件监听器
            mAMap.setOnCameraChangeListener(this);
            // 设置地图点监听击事件
            mAMap.setOnMapClickListener(this);
            // 设置地图触摸监听事件
            mAMap.setOnMapTouchListener(this);

            // 添加覆盖物
          /*  markerOption = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark2))
                    .position(new LatLng(22.493403, 114.10998))
                    .draggable(true);
            marker = mAMap.addMarker(markerOption);*/

        }
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stopLocation();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }


    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(AMapLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(AMapLocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        } else {
            //开始定位
            //   Toast.makeText(AMapLocationActivity.this,"已开启定位权限",Toast.LENGTH_LONG).show();
        }
    }

    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {

                        mAMapLocation = amapLocation;

                     /*   //定位成功回调信息，设置相关消息
                        amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表

                        amapLocation.getAccuracy();//获取精度信息
                        amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                        amapLocation.getCountry();//国家信息
                        amapLocation.getProvince();//省信息
                        amapLocation.getCity();//城市信息
                        amapLocation.getDistrict();//城区信息
                        amapLocation.getStreet();//街道信息
                        amapLocation.getStreetNum();//街道门牌号信息
                        amapLocation.getCityCode();//城市编码
                        amapLocation.getAdCode();//地区编码
                        amapLocation.getAoiName();//获取当前定位点的AOI信息

                        Log.e("sss", "xxx 当前位置（经纬度） = " + amapLocation.getLatitude() + ":" + amapLocation.getLongitude() + " 当前位置在：" + amapLocation.getAddress());*/

                        // 设置覆盖物

                        if (followMove) {
                            if (mAMap != null) {
                                mAMap.clear();
                            }

                            // 保存定位地址，用于报警
                            addressName = amapLocation.getDistrict() + amapLocation.getStreet() + amapLocation.getAoiName() + amapLocation.getStreetNum();

                            // 设置定位点覆盖物
                            String address = amapLocation.getDistrict() + amapLocation.getStreet() + amapLocation.getAoiName() + amapLocation.getStreetNum();
                            markerOption = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark2))
                                    .title("当前位置").snippet(address).position(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()))
                                    .draggable(false);
                            marker = mAMap.addMarker(markerOption);
                            marker.showInfoWindow();

                            // 设置可移动覆盖物
                            // 设置定位点覆盖物
                            String moveAddress = amapLocation.getLatitude() + "," + amapLocation.getLongitude();
                            moveMarkerOption = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark3))
                                    .title("当前位置").snippet(moveAddress).position(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()))
                                    .draggable(true);
                            moveMarker = mAMap.addMarker(moveMarkerOption);
                            //  moveMarker.setAlpha(0);
                            moveMarker.setVisible(false);
                            moveMarker.hideInfoWindow();

                         //   followMove = false;

                               /*    if (followMove) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
                        }*/
                            currentLatLng =  new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                            tvLocation.setText(currentLatLng.toString());
                            // 设置地图中心点
                            mAMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(currentLatLng, 16, 0, 0)));
                        }

                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError", "location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());
                    }
                }

            }
        });

        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        //   mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        AMapLocationClientOption option = new AMapLocationClientOption();
        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(option);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
        /*         mLocationClient.stopLocation();
            mLocationClient.startLocation();*/
        }

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.stopLocation();
        mLocationClient.startLocation();
    }


    public void starLocation(View view) {
       /* //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.stopLocation();
        mLocationClient.startLocation();*/


    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "未开启定位权限，请手动到设置去开去权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

        LatLng latLng = cameraPosition.target;
        LogUtil.e("cameraPosition = " + cameraPosition.toString());

        if (markerOption != null) {
            // moveMarker.setPosition(cameraPosition.target);
            //    moveMarker.setSnippet(latLng.toString());
            LatLng latLng1 = new LatLng(latLng.latitude, latLng.longitude);
            moveMarker.setPosition(latLng1);
            tvLocation.setText(latLng.toString());
            followMove = false;

        }

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        //根据latLng编译成地理描述
        currentLatLng = cameraPosition.target;
        moveMarker.hideInfoWindow();
        LogUtil.e("onCameraChangeFinish = " + cameraPosition.toString());
        Toast.makeText(this,"当前位置：" + currentLatLng.toString(),Toast.LENGTH_LONG);
       /* LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        getAddress(latLonPoint);*/

    }


    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        LogUtil.e("onTouch = ");

        if (moveMarker != null) {
            moveMarker.setVisible(true);
            marker.hideInfoWindow();
        }
    }
}
