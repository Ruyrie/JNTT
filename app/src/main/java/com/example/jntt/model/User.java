package com.example.jntt.model;

/** 用户账号模型 */
/**
 * 项目职责：用户模型，承载账号、昵称、头像、签名和统计信息。
 * 技术说明：使用 Java/Android 基础语法完成该业务步骤。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class User {
    public String username;
    public String password;
    public String avatarUri;

    /**
     * 项目职责：创建用户模型实例，保存当前模块运行所需的上下文或初始数据。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前模块的布局、数据类和调用方使用。
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
