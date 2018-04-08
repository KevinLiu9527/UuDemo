package com.kevin.uudemo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.kevin.uudemo.address.CityPickerBuilder;
import com.kevin.uudemo.address.adapter.OnPickListener;
import com.kevin.uudemo.utils.location.BaiduMapUtils;
import com.kevin.uudemo.utils.location.LocationUtils;

/**
 * @author：KevinLiu E-mail:KevinLiu9527@163.com
 * @time：2018/3/15 12:37
 * @修改备注： Uu跑腿demo
 */
public class MainActivity extends AppCompatActivity {


    private TextView closeTv;
    private View bottomSheet;
    private BottomSheetBehavior behavior;

    private ImageView mainUserIV;
    private NavigationView mainNV;
    private DrawerLayout mainDL;


    private FrameLayout right;

    private AppBarLayout appBarLayout;
    //百度地图
    private MapView mainMV;
    private BaiduMap bdMap;
    //定位
    private LocationUtils locationUtils;
    //地址
    private TextView addressTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        intBaidu();
    }

    /**
     * 百度相关
     */
    private void intBaidu() {
        mainMV = findViewById(R.id.main_MV);
        bdMap = mainMV.getMap();
        mainMV.showZoomControls(false);
        mainMV.showScaleControl(false);
        bdMap.setMapStatus(MapStatusUpdateFactory.zoomTo(17));
        BaiduMapUtils.removeBaiduMapIcon(mainMV);
        locationUtils = new LocationUtils(MainActivity.this, mainMV);
        locationUtils.startLocatePhone();
    }


    /**
     * 初始化控件
     */
    private void initView() {
        addressTV = findViewById(R.id.main_address_TV);
        appBarLayout = findViewById(R.id.appbar);
        mainDL = findViewById(R.id.main_DL);
        mainUserIV = findViewById(R.id.main_user_IV);
        mainNV = findViewById(R.id.main_NV);
        closeTv = findViewById(R.id.close_tv);
        right = findViewById(R.id.main_FL);
        bottomSheet = findViewById(R.id.main_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        closeTv.setText("打开");
        closeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (behavior.getState() == 3) {//关闭
                    closeTv.setText("打开");
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                if (behavior.getState() == 4) {//打开
                    closeTv.setText("关闭");
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (behavior.getState() == 3) {//关闭
                    closeTv.setText("关闭");
                }
                if (behavior.getState() == 4) {//打开
                    closeTv.setText("打开");
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        mainDL.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.LEFT);
        mainUserIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainDL.openDrawer(Gravity.LEFT);
            }
        });

        mainDL.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //获取屏幕的宽高
                WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                right.layout(mainNV.getRight(), 0, mainNV.getRight() + display.getWidth(), display.getHeight());
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        //监听CollapsingToolbarLayout
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //获取屏幕的宽高
                WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                right.layout(mainNV.getRight(), 0, mainNV.getRight() + display.getWidth(), display.getHeight());
            }
        });

        //选择地址
        addressTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CityPickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())    //此方法必须调用
                        .setCurrentCity("上海")        //APP自身已定位的城市
                        .setHotCities(new String[]{"北京", "上海", "广州", "深圳"})    //指定热门城市
                        .setOnPickListener(new OnPickListener() {
                            @Override
                            public void onPick(int position, String data) {
                                addressTV.setText(data);
                            }
                        })
                        .show();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        mainMV.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mainMV.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != locationUtils) {
            locationUtils.stopLocate();
        }
        mainMV.onDestroy();
    }
}
