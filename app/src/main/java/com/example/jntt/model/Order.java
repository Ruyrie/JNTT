package com.example.jntt.model;

/** 订单模型 */
public class Order {
    public int productId;
    public String name;
    public double price;
    public String time;

    public Order(int productId, String name, double price, String time) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.time = time;
    }
}
