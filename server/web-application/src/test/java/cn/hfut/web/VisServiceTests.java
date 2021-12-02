package cn.hfut.web;

import cn.hfut.common.utils.ComputeUtils;
import cn.hfut.gpv.domain.*;
import cn.hfut.gpv.mapper.*;
import cn.hfut.gpv.service.MarkerService;
import cn.hfut.gpv.service.VisService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.proj4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
public class VisServiceTests extends BaseTests{

    @Autowired
    VisService visService;
    @Autowired(required = false)
    HousePriceMapper housePriceMapper;
    @Autowired(required = false)
    PopulationDensityMapper populationDensityMapper;
    @Autowired
    GisPipeMapper pipeMapper;
    @Autowired
    HeatLineMapper heatLineMapper;
    @Autowired
    GisSubwayMapper gisSubwayMapper;
    @Autowired
    MarkerService markerService;

    final ComputeUtils computeUtils = ComputeUtils.getInstance();
    final private CoordinateTransformFactory ctFactory;
    final private CRSFactory crsFactory;
    CoordinateTransform trans;

    public  VisServiceTests() {
        this.ctFactory = new CoordinateTransformFactory();
        this.crsFactory = new CRSFactory();
        String WGS84_PARAM = "+title=long/lat:WGS84 +proj=longlat +ellps=WGS84 +datum=WGS84 +units=degrees";
        CoordinateReferenceSystem EPSG_32650 = crsFactory.createFromName("EPSG:32650");
        CoordinateReferenceSystem WGS84 = crsFactory.createFromParameters("WGS84", WGS84_PARAM);
        this.trans = ctFactory.createTransform(WGS84, EPSG_32650);
    }

    @Test
    public void test(){
        //System.out.println(housePriceMapper.selectByPrimaryKey(1));
        for (int i = 1; i <=5 ; i++) {
            if (i==4) continue;
            insertSubway(i+"号线",i);
        }

    }

    void insertSubway(String name, Integer groupNumber) {
        GisPipe temp = new GisPipe();
        temp.setName(name);
        temp.setGroupNumber(groupNumber);
        GisSubway condition = new GisSubway();
        condition.setCityName("合肥");
        condition.setMetroName(name);
        List<GisSubway> subways = gisSubwayMapper.selectByCondition(condition);
        List<List<Double>> nodes = new ArrayList<>();
        List<HousePrice> housePriceList = housePriceMapper.listAllHouse();
        List<PopulationDensity> populationDensityList = populationDensityMapper.listAllPopulationDensity();


        List<Double> initialWeights = new ArrayList<>();
        List<Double> initialValues = new ArrayList<>();

        for (GisSubway subway : subways) {
            GisMarker marker = new GisMarker();
            marker.setName(subway.getStationName());
            marker.setLatitude(BigDecimal.valueOf(Double.parseDouble(subway.getLatitudeGd())));
            marker.setLongitude(BigDecimal.valueOf(Double.parseDouble(subway.getLongitudeGd())));
            markerService.insertSelective(marker);
            //房价的
            List<Double> hs = new ArrayList<>();
            //人口密度的
            List<Double> ps = new ArrayList<>();
            //a点代表地铁站点
            ProjCoordinate a = new ProjCoordinate(Double.parseDouble(subway.getLongitudeGd()), Double.parseDouble(subway.getLatitudeGd()));
            for (HousePrice house : housePriceList) {
                ProjCoordinate b = new ProjCoordinate(Double.parseDouble(house.getLongitude()), Double.parseDouble(house.getLatitude()));
                Double distance = computeDistance(a, b);
                if (distance < 1000.0) {
                    hs.add(house.getUnitPriceDouble());
                }
            }
            for (PopulationDensity populationDensity : populationDensityList) {
                ProjCoordinate b = new ProjCoordinate(Double.parseDouble(populationDensity.getLongitude()), Double.parseDouble(populationDensity.getLatitude()));
                Double distance = computeDistance(a, b);
                if (distance < 1000.0) {
                    ps.add(populationDensity.getDensity());
                }
            }
            //房价取平均值
            Collections.sort(hs);
            double sum = 0.0;
            for (int i = 0; i < hs.size(); i++) {
                sum+=hs.get(i);
            }
            initialWeights.add(hs.isEmpty() ? sum : sum/(hs.size()));
            //人口密度直接取和就行
            double sum2 = 0.0;
            for (double n : ps) {
                sum2 += n;
            }
            initialValues.add(sum2);
            List<Double> latLng = new ArrayList<>();
            latLng.add(Double.parseDouble(subway.getLatitudeGd()));
            latLng.add(Double.parseDouble(subway.getLongitudeGd()));
            nodes.add(latLng);
        }
        temp.setNodes(JSONArray.parseArray(JSON.toJSONString(nodes)));

        //归一化处理后映射到合适范围
        initialValues = minMaxNormalization(initialValues, 1);
        initialWeights = minMaxNormalization(initialWeights, 2);
        temp.setInitialValues(JSONArray.parseArray(JSON.toJSONString(initialValues)));
        temp.setInitialWeights(JSONArray.parseArray(JSON.toJSONString(initialWeights)));
        pipeMapper.insertSelective(temp);
        HeatLine heatLine = visService.generateHeatLine(temp.getNodes(), JSONArray.parseArray(JSON.toJSONString(initialWeights)), JSONArray.parseArray(JSON.toJSONString(initialValues)));
        heatLine.setLineNumber(groupNumber);
        heatLine.setPipeId(temp.getId());
        heatLineMapper.insertSelective(heatLine);



    }

    /**
     * 计算两点之间的距离
     * @name computeDistance
     * @param a
     * @param b
     * @return double
     */
    Double computeDistance(ProjCoordinate a, ProjCoordinate b) {
        ProjCoordinate a1 = new ProjCoordinate();
        ProjCoordinate b1 = new ProjCoordinate();
        //转换后保存到a1b1
        trans.transform(a, a1);
        trans.transform(b, b1);

        BigDecimal x1 = BigDecimal.valueOf(a1.x);
        BigDecimal y1 = BigDecimal.valueOf(a1.y);
        BigDecimal x2 = BigDecimal.valueOf(b1.x);
        BigDecimal y2 = BigDecimal.valueOf(b1.y);

        BigDecimal temp = x1.subtract(x2).pow(2).add(y1.subtract(y2).pow(2));
        return Math.sqrt(temp.doubleValue());
    }

    /**
     * 数据归一化，Min-Max标准化
     * @name minMaxNormalization
     * @param origin
     * @return java.util.List<java.lang.Double>
     */
    List<Double> minMaxNormalization(List<Double> origin, int flag) {
        //flag = 1代表是人口密度
        if (flag != 1) {
            for (int i = 0; i < origin.size(); i++) {
                if (origin.get(i) == 0.0) {
                    origin.set(i, 13000.0);
                }
            }
        }
        double min = origin.stream().min(Double::compareTo).get();
        double max = origin.stream().max(Double::compareTo).get();

        //BigDecimal min = new BigDecimal(m1);
       // BigDecimal max = new BigDecimal(m2);
        //BigDecimal temp = max.subtract(min);
        double temp = max-min;

        for (int i = 0, length = origin.size(); i < length; i++) {
            double temp1 = (origin.get(i)-min)/temp;
            //BigDecimal temp1 = BigDecimal.valueOf(origin.get(i)).subtract(min).divide(temp, RoundingMode.HALF_EVEN);
            if (flag == 1) {
                origin.set(i, temp1*500);
            } else {
                origin.set(i, temp1*20 + 6.0);
            }
        }
        return origin;
    }

    List<Double> zScoreNormalization(List<Double> origin) {
        double average = origin.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
        double sum = 0.0;
        for (double x : origin) {
            sum += Math.pow(x-average,2);
        }
        //方差
        double variance = sum/origin.size();
        //标准差
        double u = Math.sqrt(variance);
        for (int i = 0, length = origin.size(); i < length; i++) {
            origin.set(i, (origin.get(i)-average)/u);
        }
        return origin;
    }
}
