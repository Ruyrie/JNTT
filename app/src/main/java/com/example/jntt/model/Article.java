package com.example.jntt.model;

/** 文章模型 */
public class Article {
    public int id;
    public String title;
    public String content;
    public String author;
    public String time;

    public Article(int id, String title, String content, String author, String time) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.time = time;
    }
}
