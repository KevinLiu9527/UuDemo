package com.kevin.uudemo.utils.location;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.kevin.uudemo.R;


/**
 * 定位器
 */
public class LocationUtils implements OnGetGeoCoderResultListener {

    // 实时定位模式
    private static final int LOC_REAL = 2;
    // 定位监听
    public MyLocationListenner myListener = new MyLocationListenner();
    // 上下文
    private Context mCtx;
    // 定位相关
    private LocationClient mLocClient;
    // 定位模式
    private MyLocationConfiguration.LocationMode mCurrentMode;
    // 百度地图view
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    // 地理位置搜索
    private GeoCoder mSearch = null;

    // 我的位置
    private Marker myLocMarker = null;

    // 当前地址
    private String currAddr = "";


    private Marker terminalMarker;
    /**
     * 弹出覆盖物 布局相关
     */
    private View mvPop;
    // 定位地址
    private TextView addressPop;

    private InfoWindow mInfoWindow;


    /*手机图标*/
    private BitmapDescriptor myBitmap;
    // 终端坐标
    private LatLng desLatLng;
    private InfoWindow.OnInfoWindowClickListener infoWindowListener =
            new InfoWindow.OnInfoWindowClickListener() {
                public void onInfoWindowClick() {
                }
            };

    /**
     * 定位设备
     */
    public LocationUtils(Context context, MapView mMapView) {
        mCtx = context;
        this.mMapView = mMapView;
        setInitView(mCtx);
        // 定位初始化
        mLocClient = new LocationClient(context.getApplicationContext());
        mLocClient.registerLocationListener(myListener);
        this.mBaiduMap = mMapView.getMap();
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
    }

    /**
     * 初始化布局
     */
    private void setInitView(Context mCtx) {
        mvPop = LayoutInflater.from(mCtx).inflate(R.layout.pop_marker, null);
        addressPop = mvPop.findViewById(R.id.pop_address);
    }

    /**
     * 退出定位
     */
    public void stopLocate() {
        try {
            myLocMarker.remove();
            // 退出时销毁定位
            mLocClient.stop();
            // 关闭定位图层
            mBaiduMap.setMyLocationEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始定位当前手机的位置
     */
    public void startLocatePhone() {
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, null));
        // 当没有注册监听函数时，无法发起网络请求。
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll"); // 设置坐标类型-百度经纬度坐标系
        option.setOpenGps(true);// 打开gps
        mLocClient.setLocOption(option);
        mLocClient.start();

    }

    /**
     * 地理编码回调
     */
    @Override
    public void onGetGeoCodeResult(GeoCodeResult arg0) {
    }

    /**
     * 反地理编码
     */
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        // 获取当前终端地址
        currAddr = result.getPoiList().get(0).name;
        CharSequence charSequence = setAddress();
        addressPop.setText(charSequence);
        mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(mvPop),
                desLatLng, -myBitmap.getBitmap().getHeight(), infoWindowListener);
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    /**
     * 设置文字
     */
    private CharSequence setAddress() {
        CharSequence charSequence;
        String content = "<b><font color=#999999>" + "我在" + "</b><font/>"
                + "<b><font color=#000000>" + currAddr + "</b><font/>";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            charSequence = Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY);
        } else {
            charSequence = Html.fromHtml(content);
        }
        return charSequence;
    }

    /**
     * 手机定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            myBitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_location3);
            desLatLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            if (null != myLocMarker) {
                myLocMarker.remove();
            }

            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(desLatLng));
            OverlayOptions option = new MarkerOptions().position(desLatLng).icon(myBitmap);
            myLocMarker = (Marker) mBaiduMap.addOverlay(option);
            if (null == terminalMarker) {
                // 移动到定位点
                try {
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(desLatLng);
                    mBaiduMap.animateMapStatus(u, 1);
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(17));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }
}
