package cn.hfut.gpv.domain.vo;

public class ScatterVO {
    Double latitude;
    Double longitude;
    Double value;
    String color;

    public ScatterVO(Double longitude, Double latitude,  Double value, String color) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.value = value;
        this.color = color;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
