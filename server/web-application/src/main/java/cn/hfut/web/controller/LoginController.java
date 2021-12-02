package cn.hfut.web.controller;

import cn.hfut.common.controller.CommonResult;
import cn.hfut.gpv.domain.dto.LoginHistoryDTO;
import cn.hfut.gpv.service.LoginInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api")
@Api(tags = "登录相关")
public class LoginController {

    @Autowired
    LoginInfoService loginInfoService;

    @ApiOperation(value = "记录登录信息")
    @GetMapping("/login_info")
    CommonResult recordLoginInfo(@CookieValue(value = "UID", defaultValue = "404") String UID, HttpServletResponse response){

        LoginHistoryDTO historyDTO = loginInfoService.recordLoginInfo(UID);
        //第一次需设置cookie
        if ("404".equals(UID)) {
            // 创建一个 cookie对象
            Cookie cookie = new Cookie("UID", historyDTO.getIpaddr());
            // 4个月后过期
            cookie.setMaxAge(4 * 30 * 24 * 60 * 60);
            response.addCookie(cookie);

        }
        return CommonResult.success(historyDTO);
    }
}
