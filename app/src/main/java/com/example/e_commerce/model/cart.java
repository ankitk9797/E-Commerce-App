package com.example.e_commerce.model;

public class cart {
    private String pid,pname,price,quantity,discount,admin;
    public cart()
    {

    }

    public cart(String pid, String pname, String price, String quantity, String discount,String admin) {
        this.pid = pid;
        this.pname = pname;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
        this.admin=admin;
    }

    public String getPid() {
        return pid;
    }
    public String getAdmin() {
        return admin;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
