package cn.hfut.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
/**
 * @name      WebApplication
 * @desc      程序启动类
 * @author     Kawinth
 * @createTime   2021/4/29 18:20
 */
@SpringBootApplication(scanBasePackages = {"cn.hfut.web","cn.hfut.adv","cn.hfut.gpv","cn.hfut.framework"},
                    exclude = {DataSourceAutoConfiguration.class})
public class WebApplication {
    public static void main(String[] args) {

        SpringApplication.run(WebApplication.class, args);
    }
}
