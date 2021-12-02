package cn.hfut.adv.service.impl;

import cn.hfut.adv.domain.Knn;
import cn.hfut.adv.domain.dto.KnnDTO;
import cn.hfut.adv.mapper.KnnMapper;
import cn.hfut.adv.service.KnnService;
import cn.hfut.common.domain.PageResponse;
import cn.hfut.common.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;

@Service
public class KnnServiceImpl extends CommonService implements KnnService {

    @Autowired
    KnnMapper knnMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return knnMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insertSelective(Knn record) {
        return knnMapper.insertSelective(record);
    }

    @Override
    public Knn selectByPrimaryKey(Integer id) {
        return knnMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(Knn record) {
        return knnMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public PageResponse listKnnSelective(KnnDTO conditions) {
        PageResponse response = null;

        try {
            Knn temp = new Knn();
            response = listData(conditions,knnMapper,"listKnnSelective", temp);
        } catch (Exception e) {
            response = new PageResponse(0L, null, 500, "查询出错");
            e.printStackTrace();
        }

        return response;
    }
}
