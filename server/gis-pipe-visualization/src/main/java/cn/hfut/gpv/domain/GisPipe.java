package cn.hfut.gpv.domain;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("管线描述")
public class GisPipe {

    @ApiModelProperty("主键id")
    private Integer id;
    @ApiModelProperty("管线名")
    private String name;
    @ApiModelProperty("管线真实长度")
    private Double length;
    @ApiModelProperty("管经")
    private Double diameter = 400.0;
    @ApiModelProperty("材质")
    private String texture;
    @ApiModelProperty("厂商")
    private String manufacturer;
    @ApiModelProperty("GIS系统中管线颜色")
    private String lineColor = "#993333";
    @ApiModelProperty("GIS系统中管线宽度")
    private Double lineWeight = 2.0;
    @ApiModelProperty("下属节点")
    private JSONArray nodes;
    @ApiModelProperty("水流方向，1表示与nodes中元素顺序一样，-1表示相反")
    private Byte direction = 1;
    @ApiModelProperty("起始点液压")
    private Double hydraulicPressure = 0.28;
    @ApiModelProperty("起始点流量")
    private Double flow = 1000.0;
    @ApiModelProperty("摩阻系数")
    private Double frictionCoefficient = 0.1;
    @ApiModelProperty("所在组编号")
    private Integer groupNumber = 1;
    @ApiModelProperty("热力线原始点weight属性")
    private JSONArray initialWeights;
    @ApiModelProperty("热力线原始点value值")
    private JSONArray initialValues;

    @Override
    public String toString() {
        return "GisPipe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", length=" + length +
                ", diameter=" + diameter +
                ", texture='" + texture + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", lineColor='" + lineColor + '\'' +
                ", lineWeight=" + lineWeight +
                ", nodes=" + nodes +
                ", direction=" + direction +
                ", hydraulicPressure=" + hydraulicPressure +
                ", flow=" + flow +
                ", frictionCoefficient=" + frictionCoefficient +
                ", groupNumber=" + groupNumber +
                ", heatLineWeights=" + initialWeights +
                ", heatLineValues=" + initialValues +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getDiameter() {
        return diameter;
    }

    public void setDiameter(Double diameter) {
        this.diameter = diameter;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture == null ? null : texture.trim();
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer == null ? null : manufacturer.trim();
    }

    public String getLineColor() {
        return lineColor;
    }

    public void setLineColor(String lineColor) {
        this.lineColor = lineColor == null ? null : lineColor.trim();
    }

    public Double getLineWeight() {
        return lineWeight;
    }

    public void setLineWeight(Double lineWeight) {
        this.lineWeight = lineWeight;
    }

    public JSONArray getNodes() {
        return nodes;
    }

    public void setNodes(JSONArray nodes) {
        this.nodes = nodes;
    }

    public Byte getDirection() {
        return direction;
    }

    public void setDirection(Byte direction) {
        this.direction = direction;
    }

    public Double getHydraulicPressure() {
        return hydraulicPressure;
    }

    public void setHydraulicPressure(Double hydraulicPressure) {
        this.hydraulicPressure = hydraulicPressure;
    }

    public Double getFlow() {
        return flow;
    }

    public void setFlow(Double flow) {
        this.flow = flow;
    }

    public Double getFrictionCoefficient() {
        return frictionCoefficient;
    }

    public void setFrictionCoefficient(Double frictionCoefficient) {
        this.frictionCoefficient = frictionCoefficient;
    }

    public Integer getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(Integer groupNumber) {
        this.groupNumber = groupNumber;
    }

    public JSONArray getInitialWeights() {
        return initialWeights;
    }

    public void setInitialWeights(JSONArray initialWeights) {
        this.initialWeights = initialWeights;
    }

    public JSONArray getInitialValues() {
        return initialValues;
    }

    public void setInitialValues(JSONArray initialValues) {
        this.initialValues = initialValues;
    }
}