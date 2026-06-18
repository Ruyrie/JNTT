package com.example.jntt.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** 订单模型 */
/**
 * 项目职责：订单模型，承载订单号、用户、商品、数量、价格和状态。
 * 技术说明：生成统一时间文本。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class Order {
    public static final String STATUS_PENDING   = "pending";   // 待支付
    public static final String STATUS_PAID      = "paid";      // 已支付/完成
    public static final String STATUS_CANCELLED = "cancelled"; // 已取消

    public String orderId;   // 订单号
    public int productId;
    public String name;
    public double price;
    public int quantity;
    public String time;      // 下单时间 "yyyy-MM-dd HH:mm"
    public String status;    // pending / paid / cancelled

    /**
     * 项目职责：创建订单模型实例，保存当前模块运行所需的上下文或初始数据。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前模块的布局、数据类和调用方使用。
     */
    public Order(String orderId, int productId, String name, double price, int quantity, String time, String status) {
        this.orderId    = orderId;
        this.productId  = productId;
        this.name       = name;
        this.price      = price;
        this.quantity   = quantity;
        this.time       = time;
        this.status     = status;
    }

    /** 距离超时取消剩余毫秒数（24h），<=0 表示已超时 */
    /**
     * 项目职责：订单模型，承载订单号、用户、商品、数量、价格和状态。
     * 关键调用：生成统一时间文本。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    public long getRemainingMs() {
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(time);
            if (d == null) return -1;
            long deadline = d.getTime() + 24L * 60 * 60 * 1000;
            return deadline - System.currentTimeMillis();
        } catch (ParseException e) { return -1; }
    }
}
