package cn.hfut.gpv.domain.dto;

import cn.hfut.common.domain.PageParam;

public class GisMarkerDTO extends PageParam {
    private String name;

    @Override
    public String toString() {
        return "GisPipeDTO{" +
                "pageNum=" + getPageNum() +
                ", pageSize=" + getPageSize() +
                "name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
