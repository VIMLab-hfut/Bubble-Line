package cn.hfut.gpv.service.impl;

import cn.hfut.gpv.domain.GisMarker;
import cn.hfut.gpv.domain.GisPipe;
import cn.hfut.gpv.domain.GisSubway;
import cn.hfut.gpv.domain.HeatLine;
import cn.hfut.gpv.domain.dto.GisPipeDTO;
import cn.hfut.gpv.domain.vo.PipelineVO;
import cn.hfut.gpv.mapper.GisPipeMapper;
import cn.hfut.gpv.mapper.GisSubwayMapper;
import cn.hfut.gpv.mapper.HeatLineMapper;
import cn.hfut.gpv.service.MarkerService;
import cn.hfut.gpv.service.PipeService;
import cn.hfut.gpv.service.VisService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.catalina.Pipeline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PipeServiceImpl implements PipeService {

    @Autowired
    GisPipeMapper pipeMapper;
    @Autowired
    HeatLineMapper heatLineMapper;
    @Autowired
    VisService visService;
    @Autowired
    GisSubwayMapper gisSubwayMapper;
    @Autowired
    MarkerService markerService;

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class})
    public void deleteById(Integer id) {
        heatLineMapper.deleteByPipeId(id);
        pipeMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class})
    public void insertSelective(GisPipe record) {
        HeatLine heatLine = visService.generateHeatLine(record.getNodes(), record.getInitialWeights(), record.getInitialValues());
        pipeMapper.insertSelective(record);
        heatLine.setLineNumber(record.getGroupNumber());
        heatLine.setPipeId(record.getId());
        heatLineMapper.insertSelective(heatLine);
    }

    @Override
    public GisPipe selectById(Integer id) {
        return pipeMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class})
    public void updateByIdSelective(GisPipe record) {
        GisPipe originalPipe = this.selectById(record.getId());
        pipeMapper.updateByIdSelective(record);
        HeatLine heatLine = new HeatLine();
        heatLine.setPipeId(record.getId());
        if (!record.getGroupNumber().equals(originalPipe.getGroupNumber())){
            heatLine.setLineNumber(record.getGroupNumber());
        }
        if (!record.getNodes().equals(originalPipe.getNodes())) {
            HeatLine h = visService.generateHeatLine(record.getNodes(), record.getInitialWeights(), record.getInitialValues());
            heatLine.setRibbonNodes(h.getRibbonNodes());
            heatLine.setHeatLineNodes(h.getHeatLineNodes());
            heatLine.setHeatLineWeight(h.getHeatLineWeight());
        }
        heatLineMapper.updateByPipeId(heatLine);
    }

    @Override
    public List<GisPipe> listPipeSelective(GisPipeDTO condition) {
        return pipeMapper.listPipeSelective(condition);
    }

    @Override
    public List<PipelineVO> listPipelineVO() {
        return pipeMapper.listPipelineVO(null,null);
    }

    @Override
    public PipelineVO getPipelineVOById(Integer id) {
        List<PipelineVO> result = pipeMapper.listPipelineVO(id, null);
        return result.get(0);
    }

}
