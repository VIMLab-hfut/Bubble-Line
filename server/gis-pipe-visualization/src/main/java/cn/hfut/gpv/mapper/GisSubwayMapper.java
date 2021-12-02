package cn.hfut.gpv.mapper;

import cn.hfut.gpv.domain.GisSubway;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GisSubwayMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(GisSubway record);

    GisSubway selectByPrimaryKey(Integer id);

    List<GisSubway> selectByCondition(GisSubway condition);

    int updateByPrimaryKeySelective(GisSubway record);
}