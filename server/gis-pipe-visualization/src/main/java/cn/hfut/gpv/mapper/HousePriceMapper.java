package cn.hfut.gpv.mapper;

import cn.hfut.gpv.domain.HousePrice;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HousePriceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(HousePrice record);

    int insertSelective(HousePrice record);

    HousePrice selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HousePrice record);

    int updateByPrimaryKey(HousePrice record);

    List<HousePrice> listAllHouse();
}