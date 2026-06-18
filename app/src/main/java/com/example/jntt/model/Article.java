package com.example.jntt.model;

/** 文章模型 */
/**
 * 项目职责：文章数据模型，承载文章内容、作者、图片和删除占位状态。
 * 技术说明：使用 Java/Android 基础语法完成该业务步骤。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class Article {
    public int id;
    public String title;
    public String content;
    public String author;
    public String time;
    public int readCount;
    public String coverUri; // 封面图本地 URI，null 表示使用默认图
    public boolean isDeleted; // 是否已被删除（占位符）
    public String authorNickname; // 连表查询时附带的作者昵称
    public String authorAvatarUri; // 连表查询时附带的作者头像

    /**
     * 项目职责：创建文章数据模型实例，保存当前模块运行所需的上下文或初始数据。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前模块的布局、数据类和调用方使用。
     */
    public Article(int id, String title, String content, String author, String time) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.time = time;
        this.readCount = 0;
    }
}
