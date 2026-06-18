package com.example.jntt.model;

/** 购物车条目模型 */
/**
 * 项目职责：购物车条目模型，承载商品、数量、价格和选中状态。
 * 技术说明：使用 Java/Android 基础语法完成该业务步骤。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class CartItem {
    public int productId;
    public String name;
    public double price;
    public int quantity;

    /**
     * 项目职责：创建购物车条目模型实例，保存当前模块运行所需的上下文或初始数据。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前模块的布局、数据类和调用方使用。
     */
    public CartItem(int productId, String name, double price, int quantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}
