package com.example.jntt.model;

/**
 * 项目职责：商品评价模型，承载商品评价内容、图片和发布时间。
 * 技术说明：使用 Java/Android 基础语法完成该业务步骤。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class ProductComment {
    public int id;
    public int productId;
    public String username;
    public String content;
    public String images; // Comma separated URIs
    public String time;

    // Additional fields for UI
    public String nickname;
    public String avatarUri;

    /**
     * 项目职责：创建商品评价模型实例，保存当前模块运行所需的上下文或初始数据。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前模块的布局、数据类和调用方使用。
     */
    public ProductComment(int id, int productId, String username, String content, String images, String time) {
        this.id = id;
        this.productId = productId;
        this.username = username;
        this.content = content;
        this.images = images;
        this.time = time;
    }
}
