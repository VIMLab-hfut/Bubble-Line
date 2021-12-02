package cn.hfut.gpv.service;

import cn.hfut.gpv.domain.LoginInfo;
import cn.hfut.gpv.domain.dto.LoginHistoryDTO;

import java.util.List;

public interface LoginInfoService {
    /**
     * 新增系统登录日志
     *
     * @param logininfor 访问日志对象
     */
    void insertLoginInfo(LoginInfo logininfor);

    int deleteByPrimaryKey(Long infoId);

    LoginInfo selectByPrimaryKey(Long infoId);
    /**
     * 查询系统登录日志集合
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    List<LoginInfo> selectLoginInfoList(LoginInfo logininfor);

    /**
     * 解析客户端并记录,返回登录历史
     * @name recordLoginInfo
     * @param
     * @return java.lang.Integer
     */
    LoginHistoryDTO recordLoginInfo(String UID);

    /**
     * 查询历史访问记录
     *
     * @name 查询请求用户的第一次访问信息
     * @param loginInfo
     * @return cn.hfut.gpv.domain.dto.LoginHistoryDTO
     */
    LoginHistoryDTO selectFirstLoginInfo(LoginInfo loginInfo);

}
