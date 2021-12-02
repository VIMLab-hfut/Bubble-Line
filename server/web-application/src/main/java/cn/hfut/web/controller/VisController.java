package cn.hfut.web.controller;


import cn.hfut.common.controller.CommonResult;
import cn.hfut.gpv.domain.GisPipe;
import cn.hfut.gpv.domain.vo.PipelineVO;
import cn.hfut.gpv.service.VisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api")
@Api(tags = "论文中服务接口")
public class VisController {

    @Autowired
    VisService visService;

    @ApiOperation(value = "查询所有的pipeline")
    @GetMapping("/vis/pipeline/list")
    CommonResult listPipeline(){
        Map<Integer, List<PipelineVO>> groupedResult = visService.listAllPipelineGroupByGroupNumber();
        Map<String, List> result = visService.processGroupData(groupedResult);
        return CommonResult.success(result);
    }

    @ApiOperation(value = "查询指定id的管线信息")
    @GetMapping("/vis/pipeline/point")
    CommonResult getPipeById (Integer pipeId, Integer index) {
        return CommonResult.success(visService.getPointValue(pipeId, index));
    }

    @ApiOperation(value = "修改指定管线的某点的value值")
    @PostMapping("/vis/pipeline/point/update")
    CommonResult updatePoint(Integer pipeId, Integer index, Double value, Double weight) {
        visService.updatePointValue(pipeId, index, value, weight);
        return CommonResult.success("修改成功");
    }

    @ApiOperation(value = "查询地铁沿线的房价信息")
    @GetMapping("/vis/house")
    CommonResult getAllHouse() {
        return CommonResult.success(visService.listSubwayHouse("合肥"));
    }

    @ApiOperation(value = "查询地铁沿线人口密度")
    @GetMapping("/vis/population")
    CommonResult getPopulation() {
        return CommonResult.success(visService.listPopulationDensity("合肥"));
    }

    @ApiOperation(value = "查询沿线离散点")
    @GetMapping("/vis/scatter")
    CommonResult getScatters() {
        return CommonResult.success(visService.listScatter());
    }
}
