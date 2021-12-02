package cn.hfut.common.service;

import cn.hfut.common.domain.PageParam;
import cn.hfut.common.domain.PageResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @name      CommonService
 * @desc      通用Service类
 * @author     Kawinth
 * @createTime   2021/5/3 22:22
 */
public class CommonService<T,K extends Object> {

    /**
     * 通用分页查询，使用反射实现
     * @name listData
     * @param conditions 查询条件，一般条件都是字段值，所以这里直接用实体类封装
     * @param mapper 对应mapper对象
     * @param methodName mapper里的分页查询方法名
     * @return cn.hfut.common.domain.PageResponse
     */
    public PageResponse listData(PageParam conditions, T mapper, String methodName, K reternType) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        PageHelper.startPage(conditions.getPageNum(),conditions.getPageSize());
        List<K> results = (List<K>) mapper.getClass().getMethod(methodName,conditions.getClass()).invoke(mapper,conditions);
        PageInfo<K> pageInfo = new PageInfo<>(results);
        long total = pageInfo.getTotal();
        PageResponse pageResponse = new PageResponse(total, results, 200, "查询成功");
        return pageResponse;
    }
}
