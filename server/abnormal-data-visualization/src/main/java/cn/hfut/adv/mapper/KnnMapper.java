package cn.hfut.adv.mapper;

import cn.hfut.adv.domain.Knn;
import cn.hfut.adv.domain.dto.KnnDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnnMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(Knn record);

    Knn selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Knn record);

    List<Knn> listKnnSelective(KnnDTO conditions);
}