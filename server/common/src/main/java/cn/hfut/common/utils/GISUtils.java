package cn.hfut.common.utils;

import org.locationtech.proj4j.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @name      GISUtils
 * @desc
 * 本工具类用于将经纬度(WGS84下坐标)与墨卡托投影坐标互转
 * @author     Kawinth
 * @createTime   2021/7/1 16:11
 */
public class GISUtils {

    /** 饿汉式单例模式：在加载类时就进行实例化，懒汉式则是需要的时候才加载（getInstance方法里实例化）*/
    private static final GISUtils instance = new GISUtils();

    CoordinateTransform EPSG_TO_WGS84_TRANS;
    CoordinateTransform WGS84_TO_EPSG_TRANS;


    private GISUtils(){
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CRSFactory crsFactory = new CRSFactory();
        String WGS84_PARAM = "+title=long/lat:WGS84 +proj=longlat +ellps=WGS84 +datum=WGS84 +units=degrees";
        //黄山经度118 在范围114度-120度 50N分度带中
        CoordinateReferenceSystem EPSG_32650 = crsFactory.createFromName("EPSG:32650");
        CoordinateReferenceSystem WGS84 = crsFactory.createFromParameters("WGS84", WGS84_PARAM);
        this.WGS84_TO_EPSG_TRANS = ctFactory.createTransform(WGS84, EPSG_32650);
        this.EPSG_TO_WGS84_TRANS = ctFactory.createTransform(EPSG_32650, WGS84);
    }

    public static GISUtils getInstance() {
        return instance;
    }

    public List<ProjCoordinate> latLngToEPSG(List<Double[]> latLngPoints){


        List<ProjCoordinate> coordinates = new ArrayList<>();
        ProjCoordinate latLngPoint = new ProjCoordinate();
        for (Double [] latLng :latLngPoints){
            //经度
            latLngPoint.x = latLng[1];
            //纬度
            latLngPoint.y = latLng[0];
            ProjCoordinate resultPoint = new ProjCoordinate();
            WGS84_TO_EPSG_TRANS.transform(latLngPoint, resultPoint);
            coordinates.add(resultPoint);
        }
        return coordinates;
    }

    /**
     * 将EPSG坐标系的坐标转为前端所需要的格式，经纬度数组
     * @name epsgToLatLngList
     * @param points
     * @return java.util.List<java.util.List < java.lang.Double>>
     */
    public List<List<Double>> epsgToLatLngList(List<ProjCoordinate> points){

        List<List<Double>> resultPoints = new ArrayList<>();
        ProjCoordinate tempP = new ProjCoordinate();
        for (ProjCoordinate p : points) {
            EPSG_TO_WGS84_TRANS.transform(p,tempP);
            List<Double> point = new ArrayList<>();
            point.add(0, tempP.y);
            point.add(1, tempP.x);

            resultPoints.add(point);
        }
        return resultPoints;
    }

    /**
     * 将EPSG坐标系的坐标转为WGS坐标
     * @name epsgToWGS84
     * @param points
     * @return java.util.List<org.locationtech.proj4j.ProjCoordinate>
     */
    public List<ProjCoordinate> epsgToWGS84(List<ProjCoordinate> points){

        List<ProjCoordinate> resultPoints = new ArrayList<>();

        for (ProjCoordinate p : points) {
            ProjCoordinate tempP = new ProjCoordinate();
            EPSG_TO_WGS84_TRANS.transform(p,tempP);
            resultPoints.add(tempP);
        }
        return resultPoints;
    }

    /**
     * 将某个点的经纬度坐标转换为EPSG坐标系
     * @name transWGS84ToEPSG
     * @param longitude
     * @param latitude
     * @return org.locationtech.proj4j.ProjCoordinate
     */
    public ProjCoordinate transWGS84ToEPSG(double longitude, double latitude){
        ProjCoordinate originP = new ProjCoordinate();
        originP.x = longitude;
        originP.y = latitude;
        ProjCoordinate resultP = new ProjCoordinate();

        WGS84_TO_EPSG_TRANS.transform(originP,resultP);

        return resultP;
    }

    /**
     * 计算两点之间的距离
     * @name computeDistance
     * @param a
     * @param b
     * @return double
     */
    public Double computeDistance(ProjCoordinate a, ProjCoordinate b) {
        ProjCoordinate a1 = new ProjCoordinate();
        ProjCoordinate b1 = new ProjCoordinate();
        //转换后保存到a1b1
        WGS84_TO_EPSG_TRANS.transform(a, a1);
        WGS84_TO_EPSG_TRANS.transform(b, b1);

        BigDecimal x1 = BigDecimal.valueOf(a1.x);
        BigDecimal y1 = BigDecimal.valueOf(a1.y);
        BigDecimal x2 = BigDecimal.valueOf(b1.x);
        BigDecimal y2 = BigDecimal.valueOf(b1.y);

        BigDecimal temp = x1.subtract(x2).pow(2).add(y1.subtract(y2).pow(2));
        return Math.sqrt(temp.doubleValue());
    }
}