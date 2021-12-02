package cn.hfut.gpv.domain;

public class HousePrice {
    private Integer id;

    private String houseAddress;

    private String houseRegion;

    private String houseArea;

    private String totalPrice;

    private String unitPrice;

    private Double unitPriceDouble;

    private String houseRentalArea;

    private String houseLastTime;

    private String houseYears;

    private String longitude;

    private String latitude;

    private String city;

    @Override
    public String toString() {
        return "HousePrice{" +
                "id=" + id +
                ", houseAddress='" + houseAddress + '\'' +
                ", houseRegion='" + houseRegion + '\'' +
                ", houseArea='" + houseArea + '\'' +
                ", totalPrice='" + totalPrice + '\'' +
                ", unitPrice='" + unitPrice + '\'' +
                ", unitPriceDouble=" + unitPriceDouble +
                ", houseRentalArea='" + houseRentalArea + '\'' +
                ", houseLastTime='" + houseLastTime + '\'' +
                ", houseYears='" + houseYears + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", city='" + city + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getUnitPriceDouble() {
        return unitPriceDouble;
    }

    public void setUnitPriceDouble(Double unitPriceDouble) {
        this.unitPriceDouble = unitPriceDouble;
    }

    public String getHouseAddress() {
        return houseAddress;
    }

    public void setHouseAddress(String houseAddress) {
        this.houseAddress = houseAddress == null ? null : houseAddress.trim();
    }

    public String getHouseRegion() {
        return houseRegion;
    }

    public void setHouseRegion(String houseRegion) {
        this.houseRegion = houseRegion == null ? null : houseRegion.trim();
    }

    public String getHouseArea() {
        return houseArea;
    }

    public void setHouseArea(String houseArea) {
        this.houseArea = houseArea == null ? null : houseArea.trim();
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice == null ? null : totalPrice.trim();
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPriceDouble = Double.valueOf(unitPrice.substring(0, unitPrice.length() - 4));
        this.unitPrice = unitPrice == null ? null : unitPrice.trim();
    }

    public String getHouseRentalArea() {
        return houseRentalArea;
    }

    public void setHouseRentalArea(String houseRentalArea) {
        this.houseRentalArea = houseRentalArea == null ? null : houseRentalArea.trim();
    }

    public String getHouseLastTime() {
        return houseLastTime;
    }

    public void setHouseLastTime(String houseLastTime) {
        this.houseLastTime = houseLastTime == null ? null : houseLastTime.trim();
    }

    public String getHouseYears() {
        return houseYears;
    }

    public void setHouseYears(String houseYears) {
        this.houseYears = houseYears == null ? null : houseYears.trim();
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude == null ? null : longitude.trim();
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude == null ? null : latitude.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }
}