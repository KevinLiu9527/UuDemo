package com.kevin.uudemo.utils.location;

import android.view.View;
import android.widget.ImageView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author：KevinLiu
 * @E-mail:KevinLiu9527@163.com
 * @time 2017/7/6 13:15
 * 备注：百度地图计算工具栏
 */
public class BaiduMapUtils {

    /**
     * 获取转化的百度坐标系
     *
     * @param sourceLatLng
     * @return
     */
    public static LatLng getBaiduLatLng(LatLng sourceLatLng) {
        // 将GPS设备采集的原始GPS坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        return converter.convert();
    }

    /**
     * 将google地图、soso地图、aliyun地图、mapabc地图和amap地图
     *
     * @param sourceLatLng
     * @return
     */
    public static LatLng convertBaiduLatLng(LatLng sourceLatLng) {
        // 所用坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordType.COMMON);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }

    /**
     * 将百度坐标系转换成原始GPS
     *
     * @param sourceLatLng
     * @return
     */
    public static LatLng convertBaiduToGPS(LatLng sourceLatLng) {
        // 将GPS设备采集的原始GPS坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        double latitude = 2 * sourceLatLng.latitude - desLatLng.latitude;
        double longitude = 2 * sourceLatLng.longitude - desLatLng.longitude;
        BigDecimal bdLatitude = new BigDecimal(latitude);
        bdLatitude = bdLatitude.setScale(6, BigDecimal.ROUND_HALF_UP);
        BigDecimal bdLongitude = new BigDecimal(longitude);
        bdLongitude = bdLongitude.setScale(6, BigDecimal.ROUND_HALF_UP);
        return new LatLng(bdLatitude.doubleValue(), bdLongitude.doubleValue());
    }

    /**
     * 在地图上画线
     *
     * @param mBaiduMap
     * @param p1
     * @param p2
     */
    public static void drawLine(BaiduMap mBaiduMap, LatLng p1, LatLng p2) {
        LatLng bP1 = BaiduMapUtils.getBaiduLatLng(p1);
        LatLng bP2 = BaiduMapUtils.getBaiduLatLng(p2);

        List<LatLng> points = new ArrayList<LatLng>();
        points.add(bP1);
        points.add(bP2);

        OverlayOptions ooPolyline = new PolylineOptions().width(10)
                .color(0xAA4A71C5).points(points);
        mBaiduMap.addOverlay(ooPolyline);
    }

    /**
     * 去除百度地图左下角icon标志
     */
    public static void removeBaiduMapIcon(MapView mMapView) {
        int count = mMapView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ImageView) {
                child.setVisibility(View.INVISIBLE);
            }
        }
    }
}
