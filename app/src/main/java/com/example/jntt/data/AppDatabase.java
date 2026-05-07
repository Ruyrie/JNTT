package com.example.jntt.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite 数据库帮助类，管理全部业务数据表。
 * 替代原有的 SharedPreferences 持久化方案。
 */
public class AppDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME    = "jntt.db";
    private static final int    DB_VERSION = 1;

    private static AppDatabase instance;

    public static AppDatabase getInstance(Context ctx) {
        if (instance == null) instance = new AppDatabase(ctx.getApplicationContext());
        return instance;
    }

    private AppDatabase(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 用户表（含昵称和头像）
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "nickname TEXT," +
                "avatar_uri TEXT)");

        // 文章表
        db.execSQL("CREATE TABLE articles (" +
                "id INTEGER PRIMARY KEY," +
                "title TEXT NOT NULL," +
                "content TEXT NOT NULL," +
                "author TEXT NOT NULL," +
                "time TEXT NOT NULL," +
                "read_count INTEGER DEFAULT 0," +
                "cover_uri TEXT)");

        // 商品表
        db.execSQL("CREATE TABLE products (" +
                "id INTEGER PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "desc TEXT NOT NULL," +
                "price REAL NOT NULL)");

        // 购物车（每用户独立）
        db.execSQL("CREATE TABLE cart (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL," +
                "product_id INTEGER NOT NULL," +
                "name TEXT NOT NULL," +
                "price REAL NOT NULL," +
                "quantity INTEGER NOT NULL DEFAULT 1," +
                "UNIQUE(username, product_id))");

        // 订单表
        db.execSQL("CREATE TABLE orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "order_id TEXT UNIQUE NOT NULL," +
                "username TEXT NOT NULL," +
                "product_id INTEGER NOT NULL," +
                "name TEXT NOT NULL," +
                "price REAL NOT NULL," +
                "quantity INTEGER NOT NULL," +
                "time TEXT NOT NULL," +
                "status TEXT NOT NULL)");

        // 文章点赞表（UNIQUE 防止重复）
        db.execSQL("CREATE TABLE article_likes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL," +
                "article_id INTEGER NOT NULL," +
                "UNIQUE(username, article_id))");

        // 评论表
        db.execSQL("CREATE TABLE comments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "article_id INTEGER NOT NULL," +
                "username TEXT NOT NULL," +
                "content TEXT NOT NULL," +
                "time TEXT NOT NULL)");

        // 评论点赞表
        db.execSQL("CREATE TABLE comment_likes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL," +
                "comment_id INTEGER NOT NULL," +
                "UNIQUE(username, comment_id))");

        // 关注关系表
        db.execSQL("CREATE TABLE follows (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "follower TEXT NOT NULL," +
                "following TEXT NOT NULL," +
                "UNIQUE(follower, following))");

        // 创建常用查询索引
        db.execSQL("CREATE INDEX idx_articles_author ON articles(author)");
        db.execSQL("CREATE INDEX idx_comments_article ON comments(article_id)");
        db.execSQL("CREATE INDEX idx_article_likes_article ON article_likes(article_id)");
        db.execSQL("CREATE INDEX idx_follows_following ON follows(following)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 未来升级时在此处理迁移逻辑
    }
}
