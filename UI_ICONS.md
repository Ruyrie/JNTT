# 项目可修改的图片与图标整理

以下是项目中现有的所有图片与图标资源，按照类型进行分类整理。您可以根据路径找到并替换对应的文件，如果是修改 `.xml` 的矢量图或颜色、背景样式，也可以直接编辑这些文件。

## 1. 应用程序图标 (Launcher Icons)
这些文件构成了您的 APP 在手机桌面上显示的图标。要完全替换应用图标，建议替换所有对应分辨率的图像。

| 资源用途 | 文件路径 |
| :--- | :--- |
| **Play Store 高清图标** | `app/src/main/ic_launcher-playstore.png` |
| **自适应图标 (前景/背景配置)** | `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`<br>`app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml` |
| **自适应图标背景** | `app/src/main/res/drawable/ic_launcher_background.xml` |
| **图标前景内容** (各分辨率) | `app/src/main/res/mipmap-mdpi/ic_launcher_foreground.webp`<br>`app/src/main/res/mipmap-hdpi/ic_launcher_foreground.webp`<br>`app/src/main/res/mipmap-xhdpi/ic_launcher_foreground.webp`<br>`app/src/main/res/mipmap-xxhdpi/ic_launcher_foreground.webp`<br>`app/src/main/res/mipmap-xxxhdpi/ic_launcher_foreground.webp` |
| **标准方形图标** (各分辨率) | `app/src/main/res/mipmap-mdpi/ic_launcher.webp`<br>`app/src/main/res/mipmap-hdpi/ic_launcher.webp`<br>`app/src/main/res/mipmap-xhdpi/ic_launcher.webp`<br>`app/src/main/res/mipmap-xxhdpi/ic_launcher.webp`<br>`app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp` |
| **标准圆形图标** (各分辨率) | `app/src/main/res/mipmap-mdpi/ic_launcher_round.webp`<br>`app/src/main/res/mipmap-hdpi/ic_launcher_round.webp`<br>`app/src/main/res/mipmap-xhdpi/ic_launcher_round.webp`<br>`app/src/main/res/mipmap-xxhdpi/ic_launcher_round.webp`<br>`app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.webp` |

---

## 2. 界面矢量图标 (Vector Drawables)
这些是利用 XML 绘制的矢量图形，具有缩放不失真的特点，主要用于界面当中的功能图标。

| 资源用途 | 文件路径 |
| :--- | :--- |
| **评论图标（空心）** | `app/src/main/res/drawable/ic_comment_outline.xml` |
| **点赞图标（实心/已赞）** | `app/src/main/res/drawable/ic_like_filled.xml` |
| **点赞图标（空心/未赞）** | `app/src/main/res/drawable/ic_like_outline.xml` |
| **底部导航 - “头条”图标** | `app/src/main/res/drawable/ic_nav_headline.xml` |
| **底部导航 - “商城”图标** | `app/src/main/res/drawable/ic_nav_mall.xml` |
| **底部导航 - “我的”图标** | `app/src/main/res/drawable/ic_nav_mine.xml` |
| **商品占位默认图标** | `app/src/main/res/drawable/ic_product_placeholder.xml` |

---

## 3. 内容展示配图 (JPG/PNG/WEBP 静态图片)
这些主要是项目中在列表、详情展示时用到的一些默认静态图片或内容占位图。

| 资源用途 | 文件路径 |
| :--- | :--- |
| **大米商品配图** | `app/src/main/res/mipmap-xhdpi/dami.jpg` |
| **蜂蜜商品配图** | `app/src/main/res/mipmap-xhdpi/fengmi.jpg` |
| **木耳商品配图** | `app/src/main/res/mipmap-xhdpi/muer.jpg` |
| **蔬菜商品配图** | `app/src/main/res/mipmap-xhdpi/shucai.jpg` |
| **文章/通用占位图 1** | `app/src/main/res/mipmap-xhdpi/text1.jpg` |
| **文章/通用占位图 2** | `app/src/main/res/mipmap-xhdpi/text2.jpg` |
| **文章/通用占位图 3** | `app/src/main/res/mipmap-xhdpi/text3.jpg` |
| **文章/通用占位图 4** | `app/src/main/res/mipmap-xhdpi/text4.jpg` |

---

## 4. UI 形状与背景样式 (Shape Drawables)
这些是 XML 绘制的形状（比如圆角矩形、渐变背景等），不是严格意义上的“图片”，但构成了页面的主要视觉元素，如果需要修改应用主题外观可以调整此处。

| 资源用途 | 文件路径 |
| :--- | :--- |
| **登录/注册 按钮背景** | `app/src/main/res/drawable/bg_auth_button.xml` |
| **登录/注册 输入框背景** | `app/src/main/res/drawable/bg_auth_input.xml` |
| **登录页“头条”小 Logo 背景** | `app/src/main/res/drawable/bg_auth_logo.xml` |
| **底部弹窗的圆角背景** | `app/src/main/res/drawable/bg_bottom_sheet.xml` |
| **商城右下角悬浮购物车背景** | `app/src/main/res/drawable/bg_fab_cart.xml` |
| **底部导航栏背景(毛玻璃效果)** | `app/src/main/res/drawable/bg_glass_nav.xml` |
| **毛玻璃胶囊形背景** | `app/src/main/res/drawable/bg_glass_pill.xml` |
| **顶部搜索栏整体背景** | `app/src/main/res/drawable/bg_search_bar.xml` |
| **搜索框内部背景** | `app/src/main/res/drawable/bg_search_input.xml` |
| **通用标签(Tag)背景** | `app/src/main/res/drawable/bg_tag_chip.xml` |
| **文章封面渐变半透明遮罩** | `app/src/main/res/drawable/gradient_article_overlay.xml` |
| **点击的透明水波纹效果** | `app/src/main/res/drawable/ripple_transparent.xml` |
