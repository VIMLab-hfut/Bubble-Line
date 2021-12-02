package cn.hfut.gpv.service.impl;

import cn.hfut.common.utils.ComputeUtils;
import cn.hfut.common.utils.GISUtils;
import cn.hfut.gpv.domain.*;
import cn.hfut.gpv.domain.dto.GisPipeDTO;
import cn.hfut.gpv.domain.vo.PipelineVO;
import cn.hfut.gpv.domain.vo.ScatterVO;
import cn.hfut.gpv.mapper.GisSubwayMapper;
import cn.hfut.gpv.mapper.HeatLineMapper;
import cn.hfut.gpv.mapper.HousePriceMapper;
import cn.hfut.gpv.mapper.PopulationDensityMapper;
import cn.hfut.gpv.service.PipeService;
import cn.hfut.gpv.service.VisService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.models.auth.In;
import org.locationtech.proj4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class VisServiceImpl implements VisService {

    /** 最小精度，单位米 */
    final int RIBBON_ACCURACY = 50;
    final int HEAT_LINE_ACCURACY = 50;
    final ComputeUtils computeUtils = ComputeUtils.getInstance();

    @Autowired
    PipeService pipeService;
    @Autowired
    HeatLineMapper heatLineMapper;
    @Autowired
    HousePriceMapper housePriceMapper;
    @Autowired
    PopulationDensityMapper populationDensityMapper;
    @Autowired
    GisSubwayMapper gisSubwayMapper;


    /**
     * 在指定精度下填充经纬度点，线性插值法
     * @name   fillLatLngPoint
     * @param initialLine 原始的经纬度点数组
     * @param pointCounts 用于保存每个线段填充的的点数
     * @return java.util.Map<java.lang.String,java.util.List<java.util.List<java.lang.Double>>>
     */
    @Override
    public Map<String,List<List<Double>>> fillLatLngPoint(JSONArray initialLine, List<Integer> pointCounts, int LEAST_ACCURACY) {
        GISUtils gisUtils = GISUtils.getInstance();

        //将JSONObject转换为数组
        List<Double[]> nodes = JSONObject.parseArray(initialLine.toJSONString(), Double[].class);

        List<ProjCoordinate> originalPoints = gisUtils.latLngToEPSG(nodes);

        List<ProjCoordinate> filledPoints = new ArrayList<>();
        List<ProjCoordinate> anchorPoints = new ArrayList<>();
        int length = originalPoints.size();
        for (int i = 0; i < length - 1; i++) {
            //斜率K
            BigDecimal K = computeUtils.computeK(originalPoints.get(i), originalPoints.get(i + 1));
            //垂直线的斜率
            BigDecimal K_1 = computeUtils.computeK_1(K);
            //x轴上的偏移量，即2个填充单元的间隔
            double interval = computeUtils.computeIntervalX(LEAST_ACCURACY, originalPoints.get(i), originalPoints.get(i + 1));
            double interval_1 = 20.0;

            double x1 = originalPoints.get(i).x;
            double x2 = originalPoints.get(i+1).x;
            double x = x1+interval;

            filledPoints.add(originalPoints.get(i));
            //n用来计算当直线垂直x轴时，y值的偏移量
            int n = 1;
            //两个点之间的线段数，从1开始
            int count = 1;
            while (x1 <= x2 ? x < x2 : x > x2){
                ProjCoordinate temp = new ProjCoordinate();
                ProjCoordinate anchor1 = new ProjCoordinate();
                ProjCoordinate anchor2 = new ProjCoordinate();

                temp.x = x;
                //xA,xB为对称直线的x值
                double xA = x-interval_1;
                double xB = x+interval_1;
                anchor1.x = xA;
                anchor2.x = xB;
                if (K != null) {
                    temp.y = computeUtils.computeY(x,originalPoints.get(i),K);
                    anchor1.y = computeUtils.computeY(xA,temp,K_1);
                    anchor2.y = computeUtils.computeY(xB,temp,K_1);
                } else {
                    temp.y = originalPoints.get(i).y + n*LEAST_ACCURACY;
                    anchor1.y = originalPoints.get(i).y + n*interval_1;
                    anchor2.y = originalPoints.get(i).y + n*interval_1;
                }
                filledPoints.add(temp);
                anchorPoints.add(anchor1);
                anchorPoints.add(anchor2);
                n++;
                count++;
                x += interval;
            }
            pointCounts.add(count);
        }
        filledPoints.add(originalPoints.get(length-1));

        List<List<Double>> resultLatLngList = gisUtils.epsgToLatLngList(filledPoints);
        List<List<Double>> anchorList = gisUtils.epsgToLatLngList(anchorPoints);

        Map<String,List<List<Double>>> result = new HashMap<>();
        //填充后的节点
        result.put("node", resultLatLngList);
        //生成线两侧的锚点
        result.put("anchor", anchorList);
        return result;
    }

    @Override
    public HeatLine generateHeatLine(JSONArray initialPoint, JSONArray initialWeights, JSONArray initialValues) {

        //将JSONArray转换为List
        List<Double> initialWeightList = JSONObject.parseArray(initialWeights.toJSONString(), Double.class);
        List<Double> initialValueList =  JSONObject.parseArray(initialValues.toJSONString(), Double.class);

        DecimalFormat df = new DecimalFormat("######0.00");
        //用于保存每条线段填充了多少点
        List<Integer> pointCounts = new ArrayList<>();
        //轮廓图的，无用的
        List<Integer> pointCounts2 = new ArrayList<>();

        //填充经纬度点
        List<List<Double>> filledHeatLineNodes = this.fillLatLngPoint(initialPoint, pointCounts, HEAT_LINE_ACCURACY).get("node");
        List<List<Double>> filledRibbonNodes = this.fillLatLngPoint(initialPoint, pointCounts2, RIBBON_ACCURACY).get("node");

        List<Double> filledWeight = new ArrayList<>();
        filledHeatLineNodes.get(0).add(initialValueList.get(0));
        filledWeight.add(initialWeightList.get(0));
        int i = 0;
        //k最大为原始点的个数-1
        int k = 0;
        //填充后数组的index，0已经处理过，从1开始
        int filledIndex = 1;


        for (int count : pointCounts) {
            double leftWeight = initialWeightList.get(k);
            double rightWeight = initialWeightList.get(k+1);
            double leftValue = initialValueList.get(k);
            double rightValue = initialValueList.get(k+1);
            k++;

            double weightAdd = (rightWeight - leftWeight) / count;
            double valueAdd = (rightValue - leftValue) /count;

            for (int j = 0; j < count; j++) {
                leftWeight += weightAdd;
                leftValue += valueAdd;
                filledHeatLineNodes.get(filledIndex).add(leftValue);
                filledWeight.add(leftWeight);
                filledIndex++;
            }
        }

        Map<String,List> result = new HashMap<>();

        //将List转换为JSONArray
        JSONArray ribbonNodes = JSONArray.parseArray(JSON.toJSONString(filledRibbonNodes));
        JSONArray heatLineNodes = JSONArray.parseArray(JSON.toJSONString(filledHeatLineNodes));
        JSONArray heatLineWeight = JSONArray.parseArray(JSON.toJSONString(filledWeight));
        HeatLine heatLine = new HeatLine(ribbonNodes,heatLineNodes,heatLineWeight);
        return heatLine;
    }


    /**
     * 列出所有的管线，并用分组编号作为依据来进行分组
     * @name listAllPipelineGroupByGroupNumber
     * @return java.util.Map<java.lang.Integer, java.util.List < cn.hfut.gpv.domain.vo.PipelineVO>> 键值为组号
     */
    @Override
    public Map<Integer, List<PipelineVO>> listAllPipelineGroupByGroupNumber() {
        List<PipelineVO> list = pipeService.listPipelineVO();
        Map<Integer, List<PipelineVO>> result = list.stream().collect(Collectors.groupingBy(
                //基于组编号分组
                PipelineVO::getGroupNumber,
                //分组结果容器为HashMap
                (Supplier<Map<Integer, List<PipelineVO>>>) HashMap::new,
                //每一组用List存放
                Collectors.toList())
        );
        return result;
    }

    /**
     * 将分组结果转换一下格式
     * @name processGroupData
     * @param groupResult key为分组编号，value为对应组的所有管线信息
     * @return java.util.Map<java.lang.String, java.util.List>
     */
    @Override
    public Map<String, List> processGroupData(Map<Integer, List<PipelineVO>> groupResult) {
        List<PipelineVO> pipelines = new ArrayList<>();
        List<JSONObject> heatLines = new ArrayList<>();
        List<JSONArray> ribbons = new ArrayList<>();
        //为了前端方便，将每组外轮廓的颜色单独拿出来
        List<String> colors = new ArrayList<>();
        for (Integer groupNumber : groupResult.keySet()){
            JSONArray tempRibbon = new JSONArray();
            colors.add(groupResult.get(groupNumber).get(0).getLineColor());
            for (PipelineVO pipelineVO : groupResult.get(groupNumber)){
                JSONObject tempHeatLine = new JSONObject();

                tempRibbon.addAll(pipelineVO.getRibbonNodes());
                tempHeatLine.put("node", pipelineVO.getHeatLineNodes().clone());
                tempHeatLine.put("weight", pipelineVO.getHeatLineWeight().clone());
                //转换完了后全部置为null,避免重复传输占用资源
                pipelineVO.setRibbonNodes(null);
                pipelineVO.setHeatLineNodes(null);
                pipelineVO.setHeatLineWeight(null);

                pipelines.add(pipelineVO);
                heatLines.add(tempHeatLine);
            }
            ribbons.add(tempRibbon);
        }

        Map<String, List> result = new HashMap<>();
        result.put("pipelines", pipelines);
        result.put("heatLines", heatLines);
        result.put("ribbons", ribbons);
        result.put("colors", colors);

        return result;
    }

    @Override
    public Map<String, Double> getPointValue(Integer pipeId, Integer index) {
        PipelineVO pipe = pipeService.getPipelineVOById(pipeId);
        System.out.println(pipe);
        JSONArray value = pipe.getHeatLineNodes().getJSONArray(index);
        Map<String, Double> result = new HashMap<>();
        result.put("value", Double.valueOf(value.get(2).toString()));
        result.put("weight", Double.valueOf(pipe.getHeatLineWeight().get(index).toString()));
        return result;
    }

    @Override
    public void updatePointValue(Integer pipeId, Integer index, Double value, Double weight) {
        PipelineVO pipe = pipeService.getPipelineVOById(pipeId);
        JSONArray heatLineValues = pipe.getHeatLineNodes();
        JSONArray heatLineWeights = pipe.getHeatLineWeight();
        JSONArray tempValue = heatLineValues.getJSONArray(index);
        tempValue.set(2, value);
        heatLineValues.set(index, tempValue);
        heatLineWeights.set(index, weight);

        HeatLine heatLine = new HeatLine();
        heatLine.setId(pipeId);
        heatLine.setHeatLineNodes(heatLineValues);
        heatLine.setHeatLineWeight(heatLineWeights);
        int result = heatLineMapper.updateByIdSelective(heatLine);

    }

    @Override
    public List<HousePrice> listSubwayHouse(String city) {
        GISUtils gisUtils = GISUtils.getInstance();
        GisSubway condition = new GisSubway();
        condition.setCityName(city);
        List<GisSubway> subways = gisSubwayMapper.selectByCondition(condition);
        List<HousePrice> housePriceList = housePriceMapper.listAllHouse();
        List<HousePrice> result = new ArrayList<>();

        for (GisSubway subway : subways) {
            //a点代表地铁站点
            ProjCoordinate a = new ProjCoordinate(Double.parseDouble(subway.getLongitudeGd()), Double.parseDouble(subway.getLatitudeGd()));
            for (HousePrice house : housePriceList) {
                ProjCoordinate b = new ProjCoordinate(Double.parseDouble(house.getLongitude()), Double.parseDouble(house.getLatitude()));
                if (gisUtils.computeDistance(a, b) < 1000.0) {
                    result.add(house);
                }
            }
        }
        return result;
    }

    @Override
    public List<PopulationDensity> listPopulationDensity(String city) {
        GISUtils gisUtils = GISUtils.getInstance();
        GisSubway condition = new GisSubway();
        condition.setCityName(city);
        List<GisSubway> subways = gisSubwayMapper.selectByCondition(condition);
        List<PopulationDensity> populationDensityList = populationDensityMapper.listAllPopulationDensity();
        List<PopulationDensity> result = new ArrayList<>();

        for (GisSubway subway : subways) {
            //a点代表地铁站点
            ProjCoordinate a = new ProjCoordinate(Double.parseDouble(subway.getLongitudeGd()), Double.parseDouble(subway.getLatitudeGd()));
            for (PopulationDensity populationDensity : populationDensityList) {
                ProjCoordinate b = new ProjCoordinate(Double.parseDouble(populationDensity.getLongitude()), Double.parseDouble(populationDensity.getLatitude()));
                if (gisUtils.computeDistance(a, b) < 1000.0) {
                    result.add(populationDensity);
                }
            }
        }
        return result;
    }

    @Override
    public List<ScatterVO> listScatter() {
        GISUtils gisUtils = GISUtils.getInstance();
        List<ScatterVO> result = new ArrayList<>();

        List<GisPipe> pipelineList = pipeService.listPipeSelective(new GisPipeDTO());
        //所有的房价
        List<HousePrice> housePriceList = housePriceMapper.listAllHouse();

        GisSubway subwaySelectCondition = new GisSubway();
        subwaySelectCondition.setCityName("合肥");
        for (GisPipe pipeline : pipelineList) {
            subwaySelectCondition.setMetroName(pipeline.getName());
            List<GisSubway> subways = gisSubwayMapper.selectByCondition(subwaySelectCondition);
            for (GisSubway subway : subways) {
                //a点代表地铁站点
                ProjCoordinate a = new ProjCoordinate(Double.parseDouble(subway.getLongitudeGd()), Double.parseDouble(subway.getLatitudeGd()));
                for (HousePrice house : housePriceList) {
                    ProjCoordinate b = new ProjCoordinate(Double.parseDouble(house.getLongitude()), Double.parseDouble(house.getLatitude()));
                    if (gisUtils.computeDistance(a, b) < 1000.0) {
                        result.add(new ScatterVO(Double.parseDouble(house.getLongitude()), Double.parseDouble(house.getLatitude()), house.getUnitPriceDouble(), pipeline.getLineColor()));
                    }
                }
            }
        }

        return result;
    }

}

