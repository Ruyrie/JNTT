package com.example.jntt.model;

/** 评论模型 */
/**
 * 项目职责：文章评论模型，承载评论内容、作者、时间和点赞状态。
 * 技术说明：使用 Java/Android 基础语法完成该业务步骤。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class Comment {
    public int id;
    public int articleId;
    public String username;
    public String nickname;
    public String avatarUri;
    public String content;
    public String time;
    public int likeCount;
    public boolean isLikedByMe; // 查询时根据当前登录用户设置，不持久化

    /**
     * 项目职责：创建文章评论模型实例，保存当前模块运行所需的上下文或初始数据。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前模块的布局、数据类和调用方使用。
     */
    public Comment() {
    }

    /**
     * 项目职责：创建文章评论模型实例，保存当前模块运行所需的上下文或初始数据。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前模块的布局、数据类和调用方使用。
     */
    public Comment(int id, int articleId, String username,
            String content, String time, int likeCount) {
        this.id = id;
        this.articleId = articleId;
        this.username = username;
        this.content = content;
        this.time = time;
        this.likeCount = likeCount;
    }
}
