package com.example.jntt.model;

/** 商品模型 */
/**
 * 项目职责：商品模型，承载商品名称、介绍、价格和图片 URI。
 * 技术说明：使用 Java/Android 基础语法完成该业务步骤。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class Product {
    public int id;
    public String name;
    public String desc;
    public double price;
    public String coverUri;

    /**
     * 项目职责：创建商品模型实例，保存当前模块运行所需的上下文或初始数据。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前模块的布局、数据类和调用方使用。
     */
    public Product(int id, String name, String desc, double price) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.price = price;
    }

    /**
     * 项目职责：创建商品模型实例，保存当前模块运行所需的上下文或初始数据。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前模块的布局、数据类和调用方使用。
     */
    public Product(int id, String name, String desc, double price, String coverUri) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.coverUri = coverUri;
    }
}
