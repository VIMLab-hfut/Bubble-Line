package cn.hfut.adv.domain.dto;

import cn.hfut.common.domain.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("分页查询Knn参数")
public class KnnDTO extends PageParam {
    @ApiModelProperty(value = "地点",required = true)
    String site;
    @ApiModelProperty(value = "开始时间",dataType = "java.lang.String")
    String startTime;
    @ApiModelProperty(value = "结束时间",dataType = "java.lang.String")
    String endTime;

    @Override
    public String toString() {
        return "KnnDTO{" +
                "pageNum=" + getPageNum() +
                ", pageSize=" + getPageSize() +
                ", site='" + site + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }


    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
