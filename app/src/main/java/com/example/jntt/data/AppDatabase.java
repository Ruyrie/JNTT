package com.example.jntt.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite 数据库帮助类，管理全部业务数据表。
 * 替代原有的 SharedPreferences 持久化方案。
 */
public class AppDatabase extends SQLiteOpenHelper {

        private static final String DB_NAME = "jntt.db";
        private static final int DB_VERSION = 4;

        private static AppDatabase instance;

        /**
         * 项目职责：读取SQLite 建库类，负责创建 jntt.db 的业务表结构需要的业务数据或状态。
         * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
         * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
         */
        public static AppDatabase getInstance(Context ctx) {
                if (instance == null)
                        instance = new AppDatabase(ctx.getApplicationContext());
                return instance;
        }

        /**
         * 项目职责：创建 SQLiteOpenHelper，指定数据库文件 jntt.db 和数据库版本号。
         * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
         * 配合代码：配合 DataManager 提供 SQLiteDatabase 连接。
         */
        private AppDatabase(Context ctx) {
                super(ctx, DB_NAME, null, DB_VERSION);
        }

        /**
         * 项目职责：初始化读取SQLite 建库类，负责创建 jntt.db 的业务表结构需要的业务数据或状态，加载布局、读取业务数据并绑定用户操作。
         * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
         * 配合代码：配合当前模块的布局、数据类和调用方使用。
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
                // 用户表（含昵称和头像）
                db.execSQL("CREATE TABLE users (" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "username TEXT UNIQUE NOT NULL," +
                                "password TEXT NOT NULL," +
                                "nickname TEXT," +
                                "avatar_uri TEXT," +
                                "signature TEXT," +
                                "phone TEXT UNIQUE)");

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
                                "cover_uri TEXT," +
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

                // 商品评价表
                db.execSQL("CREATE TABLE product_comments (" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "product_id INTEGER NOT NULL," +
                                "username TEXT NOT NULL," +
                                "content TEXT NOT NULL," +
                                "images TEXT," +
                                "time TEXT NOT NULL)");

                // 创建常用查询索引
                db.execSQL("CREATE INDEX idx_articles_author ON articles(author)");
                db.execSQL("CREATE INDEX idx_comments_article ON comments(article_id)");
                db.execSQL("CREATE INDEX idx_article_likes_article ON article_likes(article_id)");
                db.execSQL("CREATE INDEX idx_follows_following ON follows(following)");
                db.execSQL("CREATE INDEX idx_product_comments_product ON product_comments(product_id)");
        }

        /**
         * 项目职责：读取SQLite 建库类，负责创建 jntt.db 的业务表结构需要的业务数据或状态。
         * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
         * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                if (oldVersion < 2) {
                        db.execSQL("ALTER TABLE users ADD COLUMN signature TEXT");
                        db.execSQL("ALTER TABLE users ADD COLUMN phone TEXT");
                        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_users_phone ON users(phone)");
                }
                if (oldVersion < 3) {
                        db.execSQL("ALTER TABLE products ADD COLUMN cover_uri TEXT");
                }
                if (oldVersion < 4) {
                        db.execSQL("CREATE TABLE IF NOT EXISTS product_comments (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                        "product_id INTEGER NOT NULL," +
                                        "username TEXT NOT NULL," +
                                        "content TEXT NOT NULL," +
                                        "images TEXT," +
                                        "time TEXT NOT NULL)");
                        db.execSQL("CREATE INDEX IF NOT EXISTS idx_product_comments_product ON product_comments(product_id)");
                }
        }
}
