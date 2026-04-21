package com.example.jntt.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.jntt.model.Article;
import com.example.jntt.model.CartItem;
import com.example.jntt.model.Order;
import com.example.jntt.model.Product;
import com.example.jntt.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据管理类，使用 SharedPreferences 持久化所有业务数据
 */
public class DataManager {

    private static final String PREF_USERS      = "pref_users";
    private static final String PREF_ARTICLES   = "pref_articles";
    private static final String PREF_PRODUCTS   = "pref_products";
    private static final String PREF_CART       = "pref_cart";
    private static final String PREF_ORDERS     = "pref_orders";
    private static final String PREF_SESSION    = "pref_session";

    private static final String KEY_USERS       = "users";
    private static final String KEY_ARTICLES    = "articles";
    private static final String KEY_PRODUCTS    = "products";
    private static final String KEY_LOGGED_USER = "logged_user";
    private static final String KEY_ADMIN_MODE  = "admin_mode";

    private static DataManager instance;
    private final Context ctx;

    private DataManager(Context ctx) {
        this.ctx = ctx.getApplicationContext();
        seedDefaultData();
    }

    public static DataManager getInstance(Context ctx) {
        if (instance == null) instance = new DataManager(ctx);
        return instance;
    }

    // ─── 账号 ────────────────────────────────────────────────────────────────

    public List<User> getUsers() {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_USERS, Context.MODE_PRIVATE);
        String json = sp.getString(KEY_USERS, "[]");
        List<User> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(new User(o.getString("username"), o.getString("password")));
            }
        } catch (JSONException ignored) {}
        return list;
    }

    private void saveUsers(List<User> users) {
        JSONArray arr = new JSONArray();
        for (User u : users) {
            JSONObject o = new JSONObject();
            try { o.put("username", u.username); o.put("password", u.password); }
            catch (JSONException ignored) {}
            arr.put(o);
        }
        ctx.getSharedPreferences(PREF_USERS, Context.MODE_PRIVATE)
           .edit().putString(KEY_USERS, arr.toString()).apply();
    }

    /** 注册新账号，返回 false 表示用户名已存在 */
    public boolean register(String username, String password) {
        List<User> users = getUsers();
        for (User u : users) {
            if (u.username.equals(username)) return false;
        }
        users.add(new User(username, password));
        saveUsers(users);
        return true;
    }

    /** 校验账号密码，成功返回 User，失败返回 null */
    public User login(String username, String password) {
        for (User u : getUsers()) {
            if (u.username.equals(username) && u.password.equals(password)) return u;
        }
        return null;
    }

    /** 修改指定账号的密码 */
    public boolean changePassword(String username, String newPassword) {
        List<User> users = getUsers();
        for (User u : users) {
            if (u.username.equals(username)) {
                u.password = newPassword;
                saveUsers(users);
                return true;
            }
        }
        return false;
    }

    /** 删除账号（不能删除当前登录账号） */
    public boolean deleteUser(String username) {
        List<User> users = getUsers();
        users.removeIf(u -> u.username.equals(username));
        saveUsers(users);
        return true;
    }

    // ─── 当前登录会话 ─────────────────────────────────────────────────────────

    public void setLoggedUser(String username) {
        ctx.getSharedPreferences(PREF_SESSION, Context.MODE_PRIVATE)
           .edit().putString(KEY_LOGGED_USER, username).apply();
    }

    public String getLoggedUser() {
        return ctx.getSharedPreferences(PREF_SESSION, Context.MODE_PRIVATE)
                  .getString(KEY_LOGGED_USER, null);
    }

    public void logout() {
        ctx.getSharedPreferences(PREF_SESSION, Context.MODE_PRIVATE)
           .edit().remove(KEY_LOGGED_USER).apply();
    }

    // ─── 用户资料（昵称 & 头像） ───────────────────────────────────────────────

    /** 获取昵称，默认返回 username */
    public String getNickname(String username) {
        return ctx.getSharedPreferences("pref_profile_" + username, Context.MODE_PRIVATE)
                  .getString("nickname", username);
    }

    public void setNickname(String username, String nickname) {
        ctx.getSharedPreferences("pref_profile_" + username, Context.MODE_PRIVATE)
           .edit().putString("nickname", nickname).apply();
    }

    /** 头像本地 URI（字符串），null 表示未设置 */
    public String getAvatarUri(String username) {
        return ctx.getSharedPreferences("pref_profile_" + username, Context.MODE_PRIVATE)
                  .getString("avatar_uri", null);
    }

    public void setAvatarUri(String username, String uri) {
        ctx.getSharedPreferences("pref_profile_" + username, Context.MODE_PRIVATE)
           .edit().putString("avatar_uri", uri).apply();
    }

    // ─── 管理员模式 ───────────────────────────────────────────────────────────

    public void setAdminMode(boolean enabled) {
        ctx.getSharedPreferences(PREF_SESSION, Context.MODE_PRIVATE)
           .edit().putBoolean(KEY_ADMIN_MODE, enabled).apply();
    }

    public boolean isAdminMode() {
        return ctx.getSharedPreferences(PREF_SESSION, Context.MODE_PRIVATE)
                  .getBoolean(KEY_ADMIN_MODE, false);
    }

    // ─── 文章 ────────────────────────────────────────────────────────────────

    public List<Article> getArticles() {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_ARTICLES, Context.MODE_PRIVATE);
        String json = sp.getString(KEY_ARTICLES, "[]");
        List<Article> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                Article a = new Article(
                    o.getInt("id"), o.getString("title"),
                    o.getString("content"), o.getString("author"),
                    o.getString("time"));
                a.readCount = o.optInt("readCount", 0);
                a.coverUri  = o.optString("coverUri", null);
                if ("null".equals(a.coverUri)) a.coverUri = null;
                list.add(a);
            }
        } catch (JSONException ignored) {}
        return list;
    }

    /** 阅读数 +1 并持久化 */
    public void incrementReadCount(int articleId) {
        List<Article> list = getArticles();
        for (Article a : list) {
            if (a.id == articleId) { a.readCount++; break; }
        }
        saveArticles(list);
    }

    private void saveArticles(List<Article> articles) {
        JSONArray arr = new JSONArray();
        for (Article a : articles) {
            JSONObject o = new JSONObject();
            try {
                o.put("id", a.id); o.put("title", a.title);
                o.put("content", a.content); o.put("author", a.author);
                o.put("time", a.time); o.put("readCount", a.readCount);
                if (a.coverUri != null) o.put("coverUri", a.coverUri);
            } catch (JSONException ignored) {}
            arr.put(o);
        }
        ctx.getSharedPreferences(PREF_ARTICLES, Context.MODE_PRIVATE)
           .edit().putString(KEY_ARTICLES, arr.toString()).apply();
    }

    public void addArticle(String title, String content, String coverUri) {
        List<Article> list = getArticles();
        int id = list.isEmpty() ? 1 : list.get(list.size() - 1).id + 1;
        String author = getLoggedUser();
        String time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm",
            java.util.Locale.getDefault()).format(new java.util.Date());
        Article a = new Article(id, title, content, author, time);
        a.coverUri = coverUri;
        list.add(a);
        saveArticles(list);
    }

    /** 返回指定作者发布的文章列表 */
    public List<Article> getArticlesByAuthor(String author) {
        List<Article> result = new ArrayList<>();
        for (Article a : getArticles()) {
            if (a.author.equals(author)) result.add(a);
        }
        return result;
    }

    // ─── 商品 ────────────────────────────────────────────────────────────────

    public List<Product> getProducts() {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_PRODUCTS, Context.MODE_PRIVATE);
        String json = sp.getString(KEY_PRODUCTS, "[]");
        List<Product> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(new Product(
                    o.getInt("id"), o.getString("name"),
                    o.getString("desc"), o.getDouble("price")));
            }
        } catch (JSONException ignored) {}
        return list;
    }

    private void saveProducts(List<Product> products) {
        JSONArray arr = new JSONArray();
        for (Product p : products) {
            JSONObject o = new JSONObject();
            try {
                o.put("id", p.id); o.put("name", p.name);
                o.put("desc", p.desc); o.put("price", p.price);
            } catch (JSONException ignored) {}
            arr.put(o);
        }
        ctx.getSharedPreferences(PREF_PRODUCTS, Context.MODE_PRIVATE)
           .edit().putString(KEY_PRODUCTS, arr.toString()).apply();
    }

    public void addProduct(String name, String desc, double price) {
        List<Product> list = getProducts();
        int id = list.isEmpty() ? 1 : list.get(list.size() - 1).id + 1;
        list.add(new Product(id, name, desc, price));
        saveProducts(list);
    }

    // ─── 购物车 ───────────────────────────────────────────────────────────────

    public List<CartItem> getCart(String username) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_CART + "_" + username, Context.MODE_PRIVATE);
        String json = sp.getString("cart", "[]");
        List<CartItem> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(new CartItem(
                    o.getInt("productId"), o.getString("name"),
                    o.getDouble("price"), o.getInt("quantity")));
            }
        } catch (JSONException ignored) {}
        return list;
    }

    public void addToCart(String username, Product product) {
        List<CartItem> list = getCart(username);
        for (CartItem item : list) {
            if (item.productId == product.id) {
                item.quantity++;
                saveCart(username, list);
                return;
            }
        }
        list.add(new CartItem(product.id, product.name, product.price, 1));
        saveCart(username, list);
    }

    public void saveCartPublic(String username, List<CartItem> items) {
        saveCart(username, items);
    }

    private void saveCart(String username, List<CartItem> items) {
        JSONArray arr = new JSONArray();
        for (CartItem c : items) {
            JSONObject o = new JSONObject();
            try {
                o.put("productId", c.productId); o.put("name", c.name);
                o.put("price", c.price); o.put("quantity", c.quantity);
            } catch (JSONException ignored) {}
            arr.put(o);
        }
        ctx.getSharedPreferences(PREF_CART + "_" + username, Context.MODE_PRIVATE)
           .edit().putString("cart", arr.toString()).apply();
    }

    // ─── 订单 ────────────────────────────────────────────────────────────────

    public List<Order> getOrders(String username) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_ORDERS + "_" + username, Context.MODE_PRIVATE);
        String json = sp.getString("orders", "[]");
        List<Order> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                Order ord = new Order(
                    o.optString("orderId", "ORD" + i),
                    o.getInt("productId"), o.getString("name"),
                    o.getDouble("price"),
                    o.optInt("quantity", 1),
                    o.getString("time"),
                    o.optString("status", Order.STATUS_PAID));
                list.add(ord);
            }
        } catch (JSONException ignored) {}
        return list;
    }

    public void addOrder(String username, int productId, String name, double price, int quantity) {
        List<Order> list = getOrders(username);
        String time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm",
            java.util.Locale.getDefault()).format(new java.util.Date());
        String orderId = "JN" + System.currentTimeMillis();
        list.add(new Order(orderId, productId, name, price, quantity, time, Order.STATUS_PENDING));
        saveOrders(username, list);
    }

    public void updateOrderStatus(String username, String orderId, String status) {
        List<Order> list = getOrders(username);
        for (Order o : list) {
            if (orderId.equals(o.orderId)) { o.status = status; break; }
        }
        saveOrders(username, list);
    }

    private void saveOrders(String username, List<Order> orders) {
        JSONArray arr = new JSONArray();
        for (Order ord : orders) {
            JSONObject o = new JSONObject();
            try {
                o.put("orderId", ord.orderId);
                o.put("productId", ord.productId); o.put("name", ord.name);
                o.put("price", ord.price); o.put("quantity", ord.quantity);
                o.put("time", ord.time); o.put("status", ord.status);
            } catch (JSONException ignored) {}
            arr.put(o);
        }
        ctx.getSharedPreferences(PREF_ORDERS + "_" + username, Context.MODE_PRIVATE)
           .edit().putString("orders", arr.toString()).apply();
    }

    // ─── 种子数据（首次启动写入示例内容） ────────────────────────────────────

    private void seedDefaultData() {
        // 只在首次启动时初始化
        SharedPreferences sp = ctx.getSharedPreferences("pref_seeded", Context.MODE_PRIVATE);
        if (sp.getBoolean("done", false)) return;

        // 示例账号
        register("admin", "123456");
        register("user1", "123456");

        // 示例文章
        List<Article> articles = new ArrayList<>();
        articles.add(new Article(1, "吉林科技学院举办科技节", "本次科技节汇聚了来自全国各地的农业科技专家，展示了最新农业技术成果，吸引了众多师生参与。", "admin", "2026-04-01 09:00"));
        articles.add(new Article(2, "新型水稻品种研发成功", "经过多年培育，我校农学院成功研发出高产、抗病新型水稻品种，亩产可达800公斤以上，为粮食安全提供有力保障。", "admin", "2026-04-05 14:30"));
        articles.add(new Article(3, "智慧农业实验基地投入使用", "学校智慧农业实验基地正式投入使用，基地配备物联网传感器、无人机等先进设备，开创农业教育新模式。", "admin", "2026-04-10 10:00"));
        articles.add(new Article(4, "农业经济论坛成功举办", "本届农业经济论坛围绕乡村振兴战略展开深入讨论，多位专家学者分享了最新研究成果和政策解读。", "user1", "2026-04-15 16:00"));
        saveArticles(articles);

        // 示例商品
        List<Product> products = new ArrayList<>();
        products.add(new Product(1, "东北大米（5kg）", "精选东北优质长粒香米，颗粒饱满，口感软糯，自然种植，无添加。", 45.00));
        products.add(new Product(2, "有机黑木耳（250g）", "长白山纯天然有机黑木耳，肉厚脆嫩，富含多糖及铁元素，营养丰富。", 38.50));
        products.add(new Product(3, "农家蜂蜜（500g）", "纯天然百花蜂蜜，无任何添加剂，每瓶均经过质量检测，香甜可口。", 68.00));
        products.add(new Product(4, "绿色蔬菜礼盒", "精选时令新鲜蔬菜组合，产自有机农场，当日采摘，新鲜直达。", 99.00));
        saveProducts(products);

        sp.edit().putBoolean("done", true).apply();
    }
}
