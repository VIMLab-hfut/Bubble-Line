package cn.hfut.adv.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;


@ApiModel("Knn对象描述")
public class Knn {
    @ApiModelProperty("主键id")
    private Integer id;
    @ApiModelProperty("时刻")
    private Date time;
    @ApiModelProperty("地点")
    private String site;
    @ApiModelProperty("真实值")
    private Double value;
    @ApiModelProperty("KNN填充的缺失值")
    private Double knn;

    @Override
    public String toString() {
        return "Knn{" +
                "id=" + id +
                ", time=" + time +
                ", site='" + site + '\'' +
                ", value=" + value +
                ", knn=" + knn +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site == null ? null : site.trim();
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getKnn() {
        return knn;
    }

    public void setKnn(Double knn) {
        this.knn = knn;
    }
}