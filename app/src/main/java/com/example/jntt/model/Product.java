package com.example.jntt.model;

/** 商品模型 */
public class Product {
    public int id;
    public String name;
    public String desc;
    public double price;

    public Product(int id, String name, String desc, double price) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.price = price;
    }
}
