package cn.hfut.gpv.service;

import cn.hfut.gpv.domain.GisMarker;
import cn.hfut.gpv.domain.dto.GisPipeDTO;

import java.util.List;

public interface MarkerService {

    int deleteById(Integer id);

    int insertSelective(GisMarker record);

    GisMarker selectById(Integer id);

    int updateByIdSelective(GisMarker record);

    List<GisMarker> listGisMarkerSelective(GisPipeDTO condition);
}
