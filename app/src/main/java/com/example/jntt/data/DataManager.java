package com.example.jntt.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.jntt.model.Article;
import com.example.jntt.model.CartItem;
import com.example.jntt.model.Comment;
import com.example.jntt.model.Order;
import com.example.jntt.model.Product;
import com.example.jntt.model.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 统一数据管理类。
 * 全部业务数据（用户/文章/商品/购物车/订单/点赞/评论/关注）存储于 SQLite。
 * 登录会话（logged_user、admin_mode）保留在 SharedPreferences（轻量、无需持久化）。
 */
public class DataManager {

    private static final String PREF_SESSION    = "pref_session";
    private static final String KEY_LOGGED_USER = "logged_user";
    private static final String KEY_ADMIN_MODE  = "admin_mode";

    private static DataManager instance;
    private final Context     ctx;
    private final AppDatabase appDb;

    private DataManager(Context ctx) {
        this.ctx   = ctx.getApplicationContext();
        this.appDb = AppDatabase.getInstance(this.ctx);
        seedDefaultData();
    }

    public static DataManager getInstance(Context ctx) {
        if (instance == null) instance = new DataManager(ctx);
        return instance;
    }

    // ─── 会话（仍用 SharedPreferences） ─────────────────────────────────────

    public void setLoggedUser(String username) {
        prefs().edit().putString(KEY_LOGGED_USER, username).apply();
    }

    public String getLoggedUser() {
        return prefs().getString(KEY_LOGGED_USER, null);
    }

    public void logout() {
        prefs().edit().remove(KEY_LOGGED_USER).apply();
    }

    public void setAdminMode(boolean enabled) {
        prefs().edit().putBoolean(KEY_ADMIN_MODE, enabled).apply();
    }

    public boolean isAdminMode() {
        return prefs().getBoolean(KEY_ADMIN_MODE, false);
    }

    private SharedPreferences prefs() {
        return ctx.getSharedPreferences(PREF_SESSION, Context.MODE_PRIVATE);
    }

    // ─── 用户 ────────────────────────────────────────────────────────────────

    public List<User> getUsers() {
        List<User> list = new ArrayList<>();
        try (Cursor c = rdb().rawQuery("SELECT username,password FROM users", null)) {
            while (c.moveToNext()) list.add(new User(c.getString(0), c.getString(1)));
        }
        return list;
    }

    /** 注册，用户名唯一，返回 false 表示已存在 */
    public boolean register(String username, String password) {
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        cv.put("nickname", username);
        return wdb().insertWithOnConflict("users", null, cv, SQLiteDatabase.CONFLICT_IGNORE) != -1;
    }

    public User login(String username, String password) {
        try (Cursor c = rdb().rawQuery(
                "SELECT username,password FROM users WHERE username=? AND password=?",
                new String[]{username, password})) {
            if (c.moveToFirst()) return new User(c.getString(0), c.getString(1));
        }
        return null;
    }

    public boolean changePassword(String username, String newPassword) {
        ContentValues cv = new ContentValues();
        cv.put("password", newPassword);
        return wdb().update("users", cv, "username=?", new String[]{username}) > 0;
    }

    public boolean deleteUser(String username) {
        wdb().delete("users", "username=?", new String[]{username});
        return true;
    }

    // ─── 用户资料 ────────────────────────────────────────────────────────────

    public String getNickname(String username) {
        try (Cursor c = rdb().rawQuery(
                "SELECT nickname FROM users WHERE username=?", new String[]{username})) {
            if (c.moveToFirst()) {
                String n = c.getString(0);
                return (n != null && !n.isEmpty()) ? n : username;
            }
        }
        return username;
    }

    public void setNickname(String username, String nickname) {
        ContentValues cv = new ContentValues();
        cv.put("nickname", nickname);
        wdb().update("users", cv, "username=?", new String[]{username});
    }

    public String getAvatarUri(String username) {
        try (Cursor c = rdb().rawQuery(
                "SELECT avatar_uri FROM users WHERE username=?", new String[]{username})) {
            if (c.moveToFirst()) return c.getString(0);
        }
        return null;
    }

    public void setAvatarUri(String username, String uri) {
        ContentValues cv = new ContentValues();
        cv.put("avatar_uri", uri);
        wdb().update("users", cv, "username=?", new String[]{username});
    }

    // ─── 文章 ────────────────────────────────────────────────────────────────

    public List<Article> getArticles() {
        return queryArticles("SELECT id,title,content,author,time,read_count,cover_uri FROM articles ORDER BY id DESC", null);
    }

    public List<Article> getArticlesByAuthor(String author) {
        return queryArticles(
            "SELECT id,title,content,author,time,read_count,cover_uri FROM articles WHERE author=? ORDER BY id DESC",
            new String[]{author});
    }

    public void addArticle(String title, String content, String coverUri) {
        int newId = nextId("articles");
        ContentValues cv = new ContentValues();
        cv.put("id",         newId);
        cv.put("title",      title);
        cv.put("content",    content);
        cv.put("author",     getLoggedUser());
        cv.put("time",       now("yyyy-MM-dd HH:mm"));
        cv.put("read_count", 0);
        if (coverUri != null) cv.put("cover_uri", coverUri);
        wdb().insert("articles", null, cv);
    }

    public void incrementReadCount(int articleId) {
        wdb().execSQL("UPDATE articles SET read_count = read_count + 1 WHERE id=?",
                new Object[]{articleId});
    }

    private List<Article> queryArticles(String sql, String[] args) {
        List<Article> list = new ArrayList<>();
        try (Cursor c = rdb().rawQuery(sql, args)) {
            while (c.moveToNext()) {
                Article a = new Article(c.getInt(0), c.getString(1), c.getString(2),
                        c.getString(3), c.getString(4));
                a.readCount = c.getInt(5);
                a.coverUri  = c.getString(6);
                list.add(a);
            }
        }
        return list;
    }

    // ─── 文章点赞 / 收藏 ─────────────────────────────────────────────────────

    public void likeArticle(String username, int articleId) {
        ContentValues cv = new ContentValues();
        cv.put("username",   username);
        cv.put("article_id", articleId);
        wdb().insertWithOnConflict("article_likes", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void unlikeArticle(String username, int articleId) {
        wdb().delete("article_likes", "username=? AND article_id=?",
                new String[]{username, String.valueOf(articleId)});
    }

    public boolean isArticleLiked(String username, int articleId) {
        try (Cursor c = rdb().rawQuery(
                "SELECT 1 FROM article_likes WHERE username=? AND article_id=?",
                new String[]{username, String.valueOf(articleId)})) {
            return c.moveToFirst();
        }
    }

    public int getArticleLikeCount(int articleId) {
        return queryCount("SELECT COUNT(*) FROM article_likes WHERE article_id=?",
                String.valueOf(articleId));
    }

    /** 返回当前用户点赞的文章列表（我的收藏） */
    public List<Article> getLikedArticles(String username) {
        return queryArticles(
            "SELECT a.id,a.title,a.content,a.author,a.time,a.read_count,a.cover_uri " +
            "FROM articles a INNER JOIN article_likes l ON a.id=l.article_id " +
            "WHERE l.username=? ORDER BY a.id DESC",
            new String[]{username});
    }

    // ─── 评论 ────────────────────────────────────────────────────────────────

    public Comment addComment(int articleId, String username, String content) {
        String time = now("MM-dd HH:mm");
        ContentValues cv = new ContentValues();
        cv.put("article_id", articleId);
        cv.put("username",   username);
        cv.put("content",    content);
        cv.put("time",       time);
        long id = wdb().insert("comments", null, cv);
        return new Comment((int) id, articleId, username, content, time, 0);
    }

    public List<Comment> getComments(int articleId, String currentUser) {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT c.id, c.article_id, c.username, c.content, c.time, " +
                     "COUNT(cl.id) AS like_count " +
                     "FROM comments c LEFT JOIN comment_likes cl ON c.id=cl.comment_id " +
                     "WHERE c.article_id=? GROUP BY c.id ORDER BY c.id ASC";
        try (Cursor c = rdb().rawQuery(sql, new String[]{String.valueOf(articleId)})) {
            while (c.moveToNext()) {
                Comment cm = new Comment(c.getInt(0), c.getInt(1), c.getString(2),
                        c.getString(3), c.getString(4), c.getInt(5));
                cm.isLikedByMe = isCommentLiked(currentUser, cm.id);
                list.add(cm);
            }
        }
        return list;
    }

    public void deleteComment(int commentId) {
        wdb().delete("comment_likes", "comment_id=?", new String[]{String.valueOf(commentId)});
        wdb().delete("comments",      "id=?",          new String[]{String.valueOf(commentId)});
    }

    public int getCommentCount(int articleId) {
        return queryCount("SELECT COUNT(*) FROM comments WHERE article_id=?",
                String.valueOf(articleId));
    }

    // ─── 评论点赞 ────────────────────────────────────────────────────────────

    public void likeComment(String username, int commentId) {
        ContentValues cv = new ContentValues();
        cv.put("username",   username);
        cv.put("comment_id", commentId);
        wdb().insertWithOnConflict("comment_likes", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void unlikeComment(String username, int commentId) {
        wdb().delete("comment_likes", "username=? AND comment_id=?",
                new String[]{username, String.valueOf(commentId)});
    }

    public boolean isCommentLiked(String username, int commentId) {
        try (Cursor c = rdb().rawQuery(
                "SELECT 1 FROM comment_likes WHERE username=? AND comment_id=?",
                new String[]{username, String.valueOf(commentId)})) {
            return c.moveToFirst();
        }
    }

    // ─── 关注 ────────────────────────────────────────────────────────────────

    public void followUser(String follower, String following) {
        ContentValues cv = new ContentValues();
        cv.put("follower",  follower);
        cv.put("following", following);
        wdb().insertWithOnConflict("follows", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void unfollowUser(String follower, String following) {
        wdb().delete("follows", "follower=? AND following=?",
                new String[]{follower, following});
    }

    public boolean isFollowing(String follower, String following) {
        try (Cursor c = rdb().rawQuery(
                "SELECT 1 FROM follows WHERE follower=? AND following=?",
                new String[]{follower, following})) {
            return c.moveToFirst();
        }
    }

    public int getFollowersCount(String username) {
        return queryCount("SELECT COUNT(*) FROM follows WHERE following=?", username);
    }

    public int getFollowingCount(String username) {
        return queryCount("SELECT COUNT(*) FROM follows WHERE follower=?", username);
    }

    /** 返回文章点赞最多的一条评论，用于首页卡片预览；无评论返回 null */
    public Comment getTopComment(int articleId) {
        String sql = "SELECT c.id, c.article_id, c.username, c.content, c.time, " +
                     "COUNT(cl.id) AS like_count " +
                     "FROM comments c LEFT JOIN comment_likes cl ON c.id=cl.comment_id " +
                     "WHERE c.article_id=? GROUP BY c.id ORDER BY like_count DESC, c.id DESC LIMIT 1";
        try (Cursor c = rdb().rawQuery(sql, new String[]{String.valueOf(articleId)})) {
            if (c.moveToFirst()) {
                return new Comment(c.getInt(0), c.getInt(1), c.getString(2),
                        c.getString(3), c.getString(4), c.getInt(5));
            }
        }
        return null;
    }

    /** 返回关注 username 的用户名列表（粉丝） */
    public List<String> getFollowers(String username) {
        List<String> list = new ArrayList<>();
        try (Cursor c = rdb().rawQuery(
                "SELECT follower FROM follows WHERE following=? ORDER BY id DESC",
                new String[]{username})) {
            while (c.moveToNext()) list.add(c.getString(0));
        }
        return list;
    }

    /** 返回 username 关注的用户名列表 */
    public List<String> getFollowing(String username) {
        List<String> list = new ArrayList<>();
        try (Cursor c = rdb().rawQuery(
                "SELECT following FROM follows WHERE follower=? ORDER BY id DESC",
                new String[]{username})) {
            while (c.moveToNext()) list.add(c.getString(0));
        }
        return list;
    }

    /** 该用户所有文章累计获得的点赞数 */
    public int getTotalLikesReceived(String username) {
        return queryCount(
            "SELECT COUNT(*) FROM article_likes al " +
            "INNER JOIN articles a ON al.article_id=a.id WHERE a.author=?", username);
    }

    // ─── 商品 ────────────────────────────────────────────────────────────────

    public List<Product> getProducts() {
        List<Product> list = new ArrayList<>();
        try (Cursor c = rdb().rawQuery(
                "SELECT id,name,desc,price FROM products", null)) {
            while (c.moveToNext())
                list.add(new Product(c.getInt(0), c.getString(1), c.getString(2), c.getDouble(3)));
        }
        return list;
    }

    public void addProduct(String name, String desc, double price) {
        int newId = nextId("products");
        ContentValues cv = new ContentValues();
        cv.put("id",    newId);
        cv.put("name",  name);
        cv.put("desc",  desc);
        cv.put("price", price);
        wdb().insert("products", null, cv);
    }

    // ─── 购物车 ───────────────────────────────────────────────────────────────

    public List<CartItem> getCart(String username) {
        List<CartItem> list = new ArrayList<>();
        try (Cursor c = rdb().rawQuery(
                "SELECT product_id,name,price,quantity FROM cart WHERE username=?",
                new String[]{username})) {
            while (c.moveToNext())
                list.add(new CartItem(c.getInt(0), c.getString(1), c.getDouble(2), c.getInt(3)));
        }
        return list;
    }

    public void addToCart(String username, Product product) {
        try (Cursor c = rdb().rawQuery(
                "SELECT quantity FROM cart WHERE username=? AND product_id=?",
                new String[]{username, String.valueOf(product.id)})) {
            if (c.moveToFirst()) {
                int qty = c.getInt(0) + 1;
                wdb().execSQL("UPDATE cart SET quantity=? WHERE username=? AND product_id=?",
                        new Object[]{qty, username, product.id});
            } else {
                ContentValues cv = new ContentValues();
                cv.put("username",   username);
                cv.put("product_id", product.id);
                cv.put("name",       product.name);
                cv.put("price",      product.price);
                cv.put("quantity",   1);
                wdb().insert("cart", null, cv);
            }
        }
    }

    public void saveCartPublic(String username, List<CartItem> items) {
        SQLiteDatabase d = wdb();
        d.delete("cart", "username=?", new String[]{username});
        for (CartItem item : items) {
            ContentValues cv = new ContentValues();
            cv.put("username",   username);
            cv.put("product_id", item.productId);
            cv.put("name",       item.name);
            cv.put("price",      item.price);
            cv.put("quantity",   item.quantity);
            d.insert("cart", null, cv);
        }
    }

    // ─── 订单 ────────────────────────────────────────────────────────────────

    public List<Order> getOrders(String username) {
        List<Order> list = new ArrayList<>();
        try (Cursor c = rdb().rawQuery(
                "SELECT order_id,product_id,name,price,quantity,time,status " +
                "FROM orders WHERE username=? ORDER BY id DESC",
                new String[]{username})) {
            while (c.moveToNext())
                list.add(new Order(c.getString(0), c.getInt(1), c.getString(2),
                        c.getDouble(3), c.getInt(4), c.getString(5), c.getString(6)));
        }
        return list;
    }

    public void addOrder(String username, int productId, String name, double price, int quantity) {
        ContentValues cv = new ContentValues();
        cv.put("order_id",   "JN" + System.currentTimeMillis());
        cv.put("username",   username);
        cv.put("product_id", productId);
        cv.put("name",       name);
        cv.put("price",      price);
        cv.put("quantity",   quantity);
        cv.put("time",       now("yyyy-MM-dd HH:mm"));
        cv.put("status",     Order.STATUS_PENDING);
        wdb().insert("orders", null, cv);
    }

    public void updateOrderStatus(String username, String orderId, String status) {
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        wdb().update("orders", cv, "username=? AND order_id=?",
                new String[]{username, orderId});
    }

    // ─── 工具方法 ─────────────────────────────────────────────────────────────

    private SQLiteDatabase rdb() { return appDb.getReadableDatabase(); }
    private SQLiteDatabase wdb() { return appDb.getWritableDatabase(); }

    private int queryCount(String sql, String arg) {
        try (Cursor c = rdb().rawQuery(sql, new String[]{arg})) {
            return c.moveToFirst() ? c.getInt(0) : 0;
        }
    }

    private int nextId(String table) {
        try (Cursor c = rdb().rawQuery(
                "SELECT COALESCE(MAX(id),0)+1 FROM " + table, null)) {
            return c.moveToFirst() ? c.getInt(0) : 1;
        }
    }

    private String now(String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date());
    }

    // ─── 种子数据（DB 空时初始化） ────────────────────────────────────────────

    private void seedDefaultData() {
        // 已有数据则跳过
        try (Cursor c = rdb().rawQuery("SELECT COUNT(*) FROM articles", null)) {
            if (c.moveToFirst() && c.getInt(0) > 0) return;
        }

        register("admin",  "123456");
        register("user1",  "123456");

        SQLiteDatabase d = wdb();

        // 示例文章
        insertArticle(d, 1, "吉林科技学院举办科技节",
            "本次科技节汇聚了来自全国各地的农业科技专家，展示了最新农业技术成果，吸引了众多师生参与。",
            "admin", "2026-04-01 09:00", 15);
        insertArticle(d, 2, "新型水稻品种研发成功",
            "经过多年培育，我校农学院成功研发出高产、抗病新型水稻品种，亩产可达800公斤以上，为粮食安全提供有力保障。",
            "admin", "2026-04-05 14:30", 8);
        insertArticle(d, 3, "智慧农业实验基地投入使用",
            "学校智慧农业实验基地正式投入使用，基地配备物联网传感器、无人机等先进设备，开创农业教育新模式。",
            "admin", "2026-04-10 10:00", 12);
        insertArticle(d, 4, "农业经济论坛成功举办",
            "本届农业经济论坛围绕乡村振兴战略展开深入讨论，多位专家学者分享了最新研究成果和政策解读。",
            "user1", "2026-04-15 16:00", 5);

        // 示例商品
        insertProduct(d, 1, "东北大米（5kg）",
            "精选东北优质长粒香米，颗粒饱满，口感软糯，自然种植，无添加。", 45.00);
        insertProduct(d, 2, "有机黑木耳（250g）",
            "长白山纯天然有机黑木耳，肉厚脆嫩，富含多糖及铁元素，营养丰富。", 38.50);
        insertProduct(d, 3, "农家蜂蜜（500g）",
            "纯天然百花蜂蜜，无任何添加剂，每瓶均经过质量检测，香甜可口。", 68.00);
        insertProduct(d, 4, "绿色蔬菜礼盒",
            "精选时令新鲜蔬菜组合，产自有机农场，当日采摘，新鲜直达。", 99.00);
    }

    private void insertArticle(SQLiteDatabase d, int id, String title, String content,
                                String author, String time, int readCount) {
        ContentValues cv = new ContentValues();
        cv.put("id", id); cv.put("title", title); cv.put("content", content);
        cv.put("author", author); cv.put("time", time); cv.put("read_count", readCount);
        d.insertWithOnConflict("articles", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void insertProduct(SQLiteDatabase d, int id, String name, String desc, double price) {
        ContentValues cv = new ContentValues();
        cv.put("id", id); cv.put("name", name); cv.put("desc", desc); cv.put("price", price);
        d.insertWithOnConflict("products", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }
}
