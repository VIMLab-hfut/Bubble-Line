package cn.hfut.web.controller;

import cn.hfut.adv.constant.HsSiteValue;
import cn.hfut.adv.domain.Knn;
import cn.hfut.adv.domain.dto.KnnDTO;
import cn.hfut.adv.service.KnnService;
import cn.hfut.common.controller.CommonResult;
import cn.hfut.common.domain.PageResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api")
@Api(tags = "异常数据可视化Api")
public class KnnController {

    @Autowired
    KnnService knnService;

    @ApiOperation(value = "请求参数说明")
    @GetMapping("/desc")
    public CommonResult getDesc(){
        return CommonResult.success(HsSiteValue.toList());
    }

    @ApiOperation(value = "分页查询Knn填充结果",
                  notes = "<span style=\"color:red;\"> 描述：</span>&nbsp;site值由/desc接口获取,pageNum和pageSize必填")
    @ApiResponses({
            @ApiResponse(code = 200, message = "操作成功", response = Knn.class)
    })
    @GetMapping("/knn/list")
    public PageResponse listKnn(KnnDTO knnDTO){
        return knnService.listKnnSelective(knnDTO);
    }

}

