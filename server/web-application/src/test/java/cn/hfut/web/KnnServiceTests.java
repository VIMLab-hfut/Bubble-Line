package cn.hfut.web;

import cn.hfut.adv.constant.HsSiteValue;
import cn.hfut.adv.domain.Knn;
import cn.hfut.adv.domain.dto.KnnDTO;
import cn.hfut.adv.service.KnnService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class KnnServiceTests extends BaseTests{

    @Autowired
    KnnService knnService;

    @Test
    public void test(){
        System.out.println(knnService);
    }

}
