package cn.hfut.web;

import cn.hfut.framework.config.dataSource.DruidConfig;
import cn.hfut.framework.config.dataSource.MasterDataSourceConfig;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * @name      BaseTests
 * @desc      基础测试类 PropertySource+Import注解可更换数据源
 * @author     Kawinth
 * @createTime   2021/5/4 11:44
 */
@SpringBootTest
//@PropertySource(value = {"classpath:application.yml"})
//@Import({DruidConfig.class, MasterDataSourceConfig.class})
public abstract class BaseTests {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    private Long time;

    @Before
    public void setUp() {
        this.time = System.currentTimeMillis();
        log.info("==============> 测试开始执行 <==============>");
    }

    @After
    public void tearDown() {
        log.info("==============>> 测试执行完成，耗时：{} ms <==============>", System.currentTimeMillis() - this.time);
    }

}