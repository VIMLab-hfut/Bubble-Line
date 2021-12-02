package cn.hfut.gpv.mapper;

import cn.hfut.gpv.domain.PopulationDensity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopulationDensityMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PopulationDensity record);

    int insertSelective(PopulationDensity record);

    PopulationDensity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PopulationDensity record);

    int updateByPrimaryKey(PopulationDensity record);

    List<PopulationDensity> listAllPopulationDensity();
}