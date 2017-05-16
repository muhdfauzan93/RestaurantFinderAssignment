package com.example.muhdfauzan.myrestaurant.model;

public class ModelMenu {
    private String m_id, r_Id, menuName, menuType, menuPrice, menuDetail;

    public ModelMenu() {
    }

    public ModelMenu(String m_id, String r_Id, String menuName, String menuType, String menuPrice, String menuDetail) {
        this.m_id = m_id;
        this.r_Id = r_Id;
        this.menuName = menuName;
        this.menuType = menuType;
        this.menuPrice = menuPrice;
        this.menuDetail = menuDetail;
    }

    public String getM_id() {
        return m_id;
    }

    public void setM_id(String m_id) {
        this.m_id = m_id;
    }

    public String getR_Id() {
        return r_Id;
    }

    public void setR_Id(String r_Id) {
        this.r_Id = r_Id;
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