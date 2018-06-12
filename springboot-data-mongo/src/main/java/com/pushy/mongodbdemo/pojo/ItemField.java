package com.pushy.mongodbdemo.pojo;

public class ItemField {

    private String id;
    private Order order;
    private String product;
    private double price;
    private int quantiry;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantiry() {
        return quantiry;
    }

    public void setQuantiry(int quantiry) {
        this.quantiry = quantiry;
    }

}
