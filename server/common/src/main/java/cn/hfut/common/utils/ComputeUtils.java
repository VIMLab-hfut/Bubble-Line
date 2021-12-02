package cn.hfut.common.utils;

import org.locationtech.proj4j.ProjCoordinate;

import java.math.BigDecimal;

public class ComputeUtils {

    private static final ComputeUtils instance = new ComputeUtils();

    private ComputeUtils(){}

    public static ComputeUtils getInstance(){
        return instance;
    }

    /**
     * 已知直线的点斜式方程y = k*(x-x0)+y0，根据x值求出y值
     * @param x
     * @param p1 点1,p1.x,p1.y代表xy值
     * @param k 斜率K
     * @return java.lang.Double
     * @name computeY
     */
    public double computeY(Double x, ProjCoordinate p1, BigDecimal k) {

        BigDecimal x0 = BigDecimal.valueOf(p1.x);
        BigDecimal y0 = BigDecimal.valueOf(p1.y);

        BigDecimal bx = new BigDecimal(x.toString());

        return k.multiply(bx.subtract(x0)).add(y0).doubleValue();
    }

    /**
     * 根据直线上两点坐标求该直线斜率K
     * @name computeK
     * @param p1 点1,p1.x,p1.y代表xy值
     * @param p2 点2
     * @return java.math.BigDecimal 如果返回会null，即代表直线垂直x轴
     */
    public BigDecimal computeK(ProjCoordinate p1, ProjCoordinate p2) {
        BigDecimal MPE = new BigDecimal("0.001");
        BigDecimal x1 = BigDecimal.valueOf(p1.x);
        BigDecimal y1 = BigDecimal.valueOf(p1.y);
        BigDecimal x2 = BigDecimal.valueOf(p2.x);
        BigDecimal y2 = BigDecimal.valueOf(p2.y);

        //垂直x轴时
        if (x2.subtract(x1).abs().compareTo(MPE) == -1) {
            return null;
        } else {
            //斜率k=(y2-y1)/(x2-x1)
            return y2.subtract(y1).divide(x2.subtract(x1),10,BigDecimal.ROUND_HALF_UP);
        }
    }

    /**
     * 求垂直直线的斜率 -1/K
     * @name computeK_1
     * @param K 斜率
     * @return java.math.BigDecimal
     */
    public BigDecimal computeK_1(BigDecimal K) {
        BigDecimal _1 = new BigDecimal("-1.0");
        BigDecimal K_1 = _1.divide(K,10,BigDecimal.ROUND_HALF_UP);
        return  K_1;
    }
    /**
     * 返回指定精度下的x轴变化间距
     *
     * @param accuracy 最小精度，单位m
     * @param p1
     * @param p2
     * @return java.lang.Double
     * @name computeIntervalX
     */
    public double computeIntervalX(int accuracy, ProjCoordinate p1, ProjCoordinate p2) {
        BigDecimal x1 = BigDecimal.valueOf(p1.x);
        BigDecimal x2 = BigDecimal.valueOf(p2.x);

        //a除以c代表cos值
        BigDecimal a = x2.subtract(x1).abs();
        BigDecimal c = new BigDecimal(computeDistance(p1, p2).toString());
        BigDecimal m = new BigDecimal(String.valueOf(accuracy));
        // 指定计算结果的精度，保留到小数点后几位，以及舍入模式
        double interval =m.multiply(a.divide(c, 10,BigDecimal.ROUND_HALF_UP)).doubleValue();
        if (x1.compareTo(x2) < 0) {
            return interval;
        } else {
            return -interval;
        }

    }

    /**
     * 计算两点的距离,EPSG:32650坐标系下单位近似为m
     *
     * @param p1
     * @param p2
     * @return java.lang.Double
     * @name computeDistance
     */
    public Double computeDistance(ProjCoordinate p1, ProjCoordinate p2) {

        BigDecimal x1 = BigDecimal.valueOf(p1.x);
        BigDecimal y1 = BigDecimal.valueOf(p1.y);
        BigDecimal x2 = BigDecimal.valueOf(p2.x);
        BigDecimal y2 = BigDecimal.valueOf(p2.y);

        BigDecimal temp = x1.subtract(x2).pow(2).add(y1.subtract(y2).pow(2));
        return Math.sqrt(temp.doubleValue());
    }

}
