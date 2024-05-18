package org.kursova;

public class Item {
    private String productNum,productName,status,type,quantity,price,date,author;

    public Item(String productNum, String productName, String status, String type, String quantity, String price, String date,String author) {
        this.productNum = productNum;
        this.productName = productName;
        this.status = status;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.date = date;
        this.author = author;
    }

    public String getProductNum() {
        return productNum;
    }

    public String getProductName() {
        return productName;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getAuthor() {
        return author;
    }

    public String getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public void setProductNum(String productNum) {
        this.productNum = productNum;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
