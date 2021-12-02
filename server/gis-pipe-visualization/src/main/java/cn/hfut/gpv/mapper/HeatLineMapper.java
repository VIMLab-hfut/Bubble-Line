package cn.hfut.gpv.mapper;

import cn.hfut.gpv.domain.HeatLine;
import org.springframework.stereotype.Repository;

@Repository
public interface HeatLineMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(HeatLine record);

    int updateByIdSelective(HeatLine record);

    HeatLine listHeatLine(HeatLine condition);

    int deleteByPipeId(Integer pipelineId);

    int updateByPipeId(HeatLine record);
}