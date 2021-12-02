package cn.hfut.gpv.service.impl;

import cn.hfut.gpv.domain.GisMarker;
import cn.hfut.gpv.domain.dto.GisPipeDTO;
import cn.hfut.gpv.mapper.GisMarkerMapper;
import cn.hfut.gpv.service.MarkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarkerServiceImpl implements MarkerService {

    @Autowired
    GisMarkerMapper gisMarkerMapper;

    @Override
    public int deleteById(Integer id) {
        return gisMarkerMapper.deleteById(id);
    }

    @Override
    public int insertSelective(GisMarker record) {
        return gisMarkerMapper.insertSelective(record);
    }

    @Override
    public GisMarker selectById(Integer id) {
        return gisMarkerMapper.selectById(id);
    }

    @Override
    public int updateByIdSelective(GisMarker record) {
        return gisMarkerMapper.updateByIdSelective(record);
    }

    @Override
    public List<GisMarker> listGisMarkerSelective(GisPipeDTO condition) {
        return gisMarkerMapper.listGisMarkerSelective(condition);
    }
}
