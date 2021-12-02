package cn.hfut.adv.service;

import cn.hfut.adv.domain.Knn;
import cn.hfut.adv.domain.dto.KnnDTO;
import cn.hfut.common.domain.PageResponse;

import java.util.List;

public interface KnnService {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(Knn record);

    Knn selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Knn record);

    PageResponse listKnnSelective(KnnDTO conditions);
}
