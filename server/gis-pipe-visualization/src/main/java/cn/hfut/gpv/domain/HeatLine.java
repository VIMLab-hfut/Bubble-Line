package cn.hfut.gpv.domain;

import com.alibaba.fastjson.JSONArray;

public class HeatLine {
    private Integer id;

    private Integer lineNumber;

    private Integer pipeId;

    private JSONArray ribbonNodes;

    private JSONArray heatLineNodes;

    private JSONArray heatLineWeight;

    public HeatLine(){}
    public HeatLine(JSONArray ribbonNodes, JSONArray heatLineNodes, JSONArray heatLineWeight){
        this.ribbonNodes = ribbonNodes;
        this.heatLineNodes = heatLineNodes;
        this.heatLineWeight = heatLineWeight;
    }

    @Override
    public String toString() {
        return "HeatLine{" +
                "id=" + id +
                ", lineNumber=" + lineNumber +
                ", pipeId=" + pipeId +
                ", ribbonNodes=" + ribbonNodes +
                ", heatLineNodes=" + heatLineNodes +
                ", heatLineWeight=" + heatLineWeight +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Integer getPipeId() {
        return pipeId;
    }

    public void setPipeId(Integer pipeId) {
        this.pipeId = pipeId;
    }

    public JSONArray getRibbonNodes() {
        return ribbonNodes;
    }

    public void setRibbonNodes(JSONArray ribbonNodes) {
        this.ribbonNodes = ribbonNodes;
    }

    public JSONArray getHeatLineNodes() {
        return heatLineNodes;
    }

    public void setHeatLineNodes(JSONArray heatLineNodes) {
        this.heatLineNodes = heatLineNodes;
    }

    public JSONArray getHeatLineWeight() {
        return heatLineWeight;
    }

    public void setHeatLineWeight(JSONArray heatLineWeight) {
        this.heatLineWeight = heatLineWeight;
    }
}