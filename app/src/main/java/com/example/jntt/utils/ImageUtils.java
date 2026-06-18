package com.example.jntt.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.jntt.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * 项目职责：图片选择工具，负责复用拍照/相册选择底部弹窗。
 * 技术说明：读取查询结果；绑定布局控件；绑定点击事件。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class ImageUtils {

    /**
     * 项目职责：OnImagePickerListener 对应的项目组件。
     * 技术说明：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public interface OnImagePickerListener {
        /**
         * 项目职责：把拍照选项回调给宿主页面启动相机。
         * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
         * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
         */
        void onTakePhoto();

        /**
         * 项目职责：把相册选项回调给宿主页面启动图片选择器。
         * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
         * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
         */
        void onPickFromGallery();
    }

    /**
     * 项目职责：展示拍照/相册底部弹窗，供文章、商品、评价、头像页面复用。
     * 关键调用：绑定布局控件；绑定点击事件。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    public static void showImagePickerDialog(Context context, String title, OnImagePickerListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_image_picker, null);

        TextView tvTitle = view.findViewById(R.id.tvPickerTitle);
        if (title != null && !title.isEmpty()) {
            tvTitle.setText(title);
        }

        view.findViewById(R.id.btnTake_photo).setOnClickListener(v -> {
            dialog.dismiss();
            listener.onTakePhoto();
        });

        view.findViewById(R.id.btnOpen_gallery).setOnClickListener(v -> {
            dialog.dismiss();
            listener.onPickFromGallery();
        });

        view.findViewById(R.id.btnPickerCancel).setOnClickListener(v -> dialog.dismiss());

        dialog.setContentView(view);
        dialog.show();
    }

    /**
     * 项目职责：图片选择工具，负责复用拍照/相册选择底部弹窗。
     * 关键调用：读取查询结果。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    public static String uriToBase64(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null)
                return null;

            // Compress and scale down to avoid large Base64 strings (Cursor window limit)
            int maxWidth = 500;
            int maxHeight = 500;
            float ratio = Math.min((float) maxWidth / bitmap.getWidth(), (float) maxHeight / bitmap.getHeight());
            int width = Math.round((float) ratio * bitmap.getWidth());
            int height = Math.round((float) ratio * bitmap.getHeight());

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
            byte[] bytes = baos.toByteArray();

            return "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 项目职责：图片选择工具，负责复用拍照/相册选择底部弹窗。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    public static void setAvatarFromBase64(ImageView imageView, String base64Str) {
        if (base64Str == null || !base64Str.startsWith("data:image")) {
            return;
        }
        try {
            String cleanBase64 = base64Str.substring(base64Str.indexOf(",") + 1);
            byte[] decodedString = Base64.decode(cleanBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setImageBitmap(decodedByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
