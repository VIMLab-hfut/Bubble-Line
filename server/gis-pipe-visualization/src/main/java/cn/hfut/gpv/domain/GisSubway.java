package cn.hfut.gpv.domain;

public class GisSubway {
    private Integer id;

    private String stationName;

    private String poiNumber;

    private String stationNameEng;

    private String longitudeGd;

    private String latitudeGd;

    private String metroName;

    private String cityName;

    private String region;

    private String longitudeBd;

    private String latitudeBd;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName == null ? null : stationName.trim();
    }

    public String getPoiNumber() {
        return poiNumber;
    }

    public void setPoiNumber(String poiNumber) {
        this.poiNumber = poiNumber == null ? null : poiNumber.trim();
    }

    public String getStationNameEng() {
        return stationNameEng;
    }

    public void setStationNameEng(String stationNameEng) {
        this.stationNameEng = stationNameEng == null ? null : stationNameEng.trim();
    }

    public String getLongitudeGd() {
        return longitudeGd;
    }

    public void setLongitudeGd(String longitudeGd) {
        this.longitudeGd = longitudeGd == null ? null : longitudeGd.trim();
    }

    public String getLatitudeGd() {
        return latitudeGd;
    }

    public void setLatitudeGd(String latitudeGd) {
        this.latitudeGd = latitudeGd == null ? null : latitudeGd.trim();
    }

    public String getMetroName() {
        return metroName;
    }

    public void setMetroName(String metroName) {
        this.metroName = metroName == null ? null : metroName.trim();
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName == null ? null : cityName.trim();
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region == null ? null : region.trim();
    }

    public String getLongitudeBd() {
        return longitudeBd;
    }

    public void setLongitudeBd(String longitudeBd) {
        this.longitudeBd = longitudeBd == null ? null : longitudeBd.trim();
    }

    public String getLatitudeBd() {
        return latitudeBd;
    }

    public void setLatitudeBd(String latitudeBd) {
        this.latitudeBd = latitudeBd == null ? null : latitudeBd.trim();
    }
}