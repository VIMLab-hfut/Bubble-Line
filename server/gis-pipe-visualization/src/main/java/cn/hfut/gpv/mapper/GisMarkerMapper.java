package cn.hfut.gpv.mapper;

import cn.hfut.gpv.domain.GisMarker;
import cn.hfut.gpv.domain.dto.GisPipeDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GisMarkerMapper {
    int deleteById(Integer id);

    int insertSelective(GisMarker record);

    GisMarker selectById(Integer id);

    int updateByIdSelective(GisMarker record);

    List<GisMarker> listGisMarkerSelective(GisPipeDTO condition);
}