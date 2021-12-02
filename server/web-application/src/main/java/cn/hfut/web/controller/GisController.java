package cn.hfut.web.controller;

import cn.hfut.common.controller.CommonResult;
import cn.hfut.gpv.domain.GisMarker;
import cn.hfut.gpv.domain.GisPipe;
import cn.hfut.gpv.domain.dto.GisPipeDTO;
import cn.hfut.gpv.service.MarkerService;
import cn.hfut.gpv.service.PipeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
@Api(tags = "Gis管网及Marker管理相关Api")
public class GisController {

    @Autowired
    PipeService pipeService;
    @Autowired
    MarkerService markerService;

    @ApiOperation(value = "添加一条管线")
    @PostMapping("/gis/pipe/add")
    CommonResult addPipe(@RequestBody GisPipe gisPipe){
        pipeService.insertSelective(gisPipe);
        return CommonResult.success("成功插入一条管线");
    }

    @ApiOperation(value = "添加一个marker")
    @PostMapping("/gis/marker/add")
    CommonResult addMarker(@RequestBody GisMarker gisMarker){
        return CommonResult.success(markerService.insertSelective(gisMarker));
    }

    @ApiOperation(value = "删除一条管线")
    @PostMapping("/gis/pipe/delete")
    CommonResult deletePipe(@ApiParam("管线id") Integer id){
        pipeService.deleteById(id);
        return CommonResult.success("删除成功");
    }

    @ApiOperation(value = "删除一个标记")
    @PostMapping("/gis/marker/delete")
    CommonResult deleteMarker(@ApiParam("标记id") Integer id){
        return CommonResult.success(markerService.deleteById(id));
    }

    @ApiOperation(value = "修改某条管线信息")
    @PostMapping("/gis/pipe/update")
    CommonResult updatePipe(@RequestBody GisPipe pipe){
        pipeService.updateByIdSelective(pipe);
        return CommonResult.success("更新成功");
    }

    @ApiOperation(value = "修改某个标记信息")
    @PostMapping("/gis/marker/update")
    CommonResult updateMarker(@RequestBody GisMarker marker){
        return CommonResult.success(markerService.updateByIdSelective(marker));
    }

    @ApiOperation(value = "查询所有地图上的标记")
    @GetMapping("/gis/marker/list")
    CommonResult listMarker(){
        return CommonResult.success(markerService.listGisMarkerSelective(new GisPipeDTO()));
    }

    @ApiOperation(value = "查询所有的管线")
    @GetMapping("/gis/pipe/list")
    CommonResult listPipe(){
        return CommonResult.success(pipeService.listPipeSelective(new GisPipeDTO()));
    }

    @ApiOperation(value = "查询某个管网的信息")
    @GetMapping("/gis/pipe")
    CommonResult getPipe(@RequestParam Integer id){
        return CommonResult.success(pipeService.selectById(id));
    }

    @ApiOperation(value = "查询指定id的标记信息")
    @GetMapping("/gis/marker")
    CommonResult getMarker(@RequestParam Integer id){
        return CommonResult.success(markerService.selectById(id));
    }

}
