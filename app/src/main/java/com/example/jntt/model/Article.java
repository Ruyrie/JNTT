package com.example.jntt.model;

/** 文章模型 */
public class Article {
    public int id;
    public String title;
    public String content;
    public String author;
    public String time;
    public int readCount;
    public String coverUri; // 封面图本地 URI，null 表示使用默认图

    public Article(int id, String title, String content, String author, String time) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.time = time;
        this.readCount = 0;
    }
}
