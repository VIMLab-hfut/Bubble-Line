package cn.hfut.gpv.mapper;

import cn.hfut.gpv.domain.LoginInfo;
import cn.hfut.gpv.domain.dto.LoginHistoryDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginInfoMapper {
    int deleteByPrimaryKey(Long infoId);

    int insertSelective(LoginInfo record);

    LoginInfo selectByPrimaryKey(Long infoId);

    /**
     * 查询系统登录日志集合
     *
     * @param loginInfo 访问日志对象
     * @return 登录记录集合
     */
    List<LoginInfo> selectLoginInfoList(LoginInfo loginInfo);

    /**
     * 查询历史访问记录
     *
     * @name 查询请求用户的第一次访问信息
     * @param loginInfo
     * @return cn.hfut.gpv.domain.dto.LoginHistoryDTO
     */
    LoginHistoryDTO selectFirstLoginInfo(LoginInfo loginInfo);

    Integer countLogin(@Param("userName") String userName, @Param("ipaddr") String ipaddr);
}