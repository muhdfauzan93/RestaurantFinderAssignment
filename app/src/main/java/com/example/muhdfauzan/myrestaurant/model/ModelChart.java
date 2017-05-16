package com.example.muhdfauzan.myrestaurant.model;


public class ModelChart {

    private String id, restId,menuName,menuType,menuPrice,menuDetail;

    public ModelChart(String id, String restId, String menuName, String menuType, String menuPrice, String menuDetail) {
        this.id = id;
        this.restId = restId;
        this.menuName = menuName;
        this.menuType = menuType;
        this.menuPrice = menuPrice;
        this.menuDetail = menuDetail;
    }

    public ModelChart() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestId() {
        return restId;
    }

    public void setRestId(String restId) {
        this.restId = restId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    public String getMenuPrice() {
        return menuPrice;
    }

    public void setMenuPrice(String menuPrice) {
        this.menuPrice = menuPrice;
    }

    public String getMenuDetail() {
        return menuDetail;
    }

    public void setMenuDetail(String menuDetail) {
        this.menuDetail = menuDetail;
    }
}
