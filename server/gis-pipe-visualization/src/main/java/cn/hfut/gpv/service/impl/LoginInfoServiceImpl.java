package cn.hfut.gpv.service.impl;

import cn.hfut.common.utils.record.AddressUtils;
import cn.hfut.common.utils.record.ServletUtils;
import cn.hfut.gpv.domain.LoginInfo;
import cn.hfut.gpv.domain.dto.LoginHistoryDTO;
import cn.hfut.gpv.mapper.LoginInfoMapper;
import cn.hfut.gpv.service.LoginInfoService;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 系统访问日志情况信息 服务层处理
 * 
 * @author ruoyi
 */
@Service
public class LoginInfoServiceImpl implements LoginInfoService
{

    @Autowired
    private LoginInfoMapper loginInfoMapper;

    /**
     * 新增系统登录日志
     * 
     * @param logininfor 访问日志对象
     */
    @Override
    public void insertLoginInfo(LoginInfo logininfor)
    {
        loginInfoMapper.insertSelective(logininfor);
    }

    @Override
    public int deleteByPrimaryKey(Long infoId) {
        return loginInfoMapper.deleteByPrimaryKey(infoId);
    }

    @Override
    public LoginInfo selectByPrimaryKey(Long infoId) {
        return loginInfoMapper.selectByPrimaryKey(infoId);
    }

    /**
     * 查询系统登录日志集合
     * 
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    @Override
    public List<LoginInfo> selectLoginInfoList(LoginInfo logininfor)
    {
        return loginInfoMapper.selectLoginInfoList(logininfor);
    }

    @Override
    public LoginHistoryDTO recordLoginInfo(String UID) {
        UserAgent  userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        String ip = ServletUtils.getIpAddr(ServletUtils.getRequest());
        String address = AddressUtils.getRealAddressByIP(ip);
        // 获取客户端操作系统
        String os = userAgent.getOperatingSystem().getName();
        // 获取客户端浏览器
        String browser = userAgent.getBrowser().getName();

        // 时间戳格式化模版
        //DateTimeFormatter DATETIME19 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 时间戳转字符串
        //String dtStr = DATETIME19.format(LocalDateTime.now());

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        LoginInfo loginInfo = new LoginInfo();
        //第一次登录直接设置ip，随后的可以用cookie中的
        if ("404".equals(UID)) {
            loginInfo.setUserName(ip);
        } else {
            loginInfo.setUserName(UID);
        }
        loginInfo.setIpaddr(ip);
        loginInfo.setLoginLocation(address);
        loginInfo.setBrowser(browser);
        loginInfo.setOs(os);

        this.insertLoginInfo(loginInfo);
        //查询第一次登录信息
        LoginHistoryDTO history = this.selectFirstLoginInfo(loginInfo);
        history.setLoginCount(loginInfoMapper.countLogin(loginInfo.getUserName(),ip));
        history.setTimeStr(sdf.format(history.getLoginTime()));
        //将操作系统项改为当前请求的操作系统
        history.setOs(os);
        return history;
    }

    @Override
    public LoginHistoryDTO selectFirstLoginInfo(LoginInfo loginInfo) {
        return loginInfoMapper.selectFirstLoginInfo(loginInfo);
    }

}
