package cn.hfut.gpv.service;

import cn.hfut.gpv.domain.GisPipe;
import cn.hfut.gpv.domain.HeatLine;
import cn.hfut.gpv.domain.HousePrice;
import cn.hfut.gpv.domain.PopulationDensity;
import cn.hfut.gpv.domain.vo.PipelineVO;
import cn.hfut.gpv.domain.vo.ScatterVO;
import com.alibaba.fastjson.JSONArray;


import java.util.List;
import java.util.Map;

public interface VisService {
    Map<String,List<List<Double>>> fillLatLngPoint(JSONArray initLine, List<Integer> pointCounts, int LEAST_ACCURACY);

    HeatLine generateHeatLine(JSONArray initialPoint, JSONArray initialWeights, JSONArray initialValues);

    /**
     * 列出所有的管线，并用分组编号作为依据来进行分组
     * @name listAllPipelineGroupByGroupNumber
     * @return java.util.Map<java.lang.Integer, java.util.List <cn.hfut.gpv.domain.vo.PipelineVO>> 键值为组号
     */
    Map<Integer, List<PipelineVO>> listAllPipelineGroupByGroupNumber();

    /**
     * 将分组结果转换一下格式
     * @name processGroupData
     * @param groupResult
     * @return java.util.Map<java.lang.String, java.util.List>
     */
    Map<String, List> processGroupData(Map<Integer, List<PipelineVO>> groupResult);

    Map<String, Double> getPointValue(Integer pipeId, Integer index);

    void updatePointValue(Integer pipeId, Integer index, Double value, Double weight);

    List<HousePrice> listSubwayHouse(String city);

    List<PopulationDensity> listPopulationDensity(String city);

    List<ScatterVO> listScatter();
}
