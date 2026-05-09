package com.example.jntt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.example.jntt.data.DataManager;
import java.io.File;

public class ProfileEditActivity extends AppCompatActivity {

    private DataManager dm;
    private String username;
    private String pendingAvatarUri;
    private String pendingAvatarBase64;
    private TextView tvPhone;
    private TextView tvBindPhoneBtn;

    private Uri currentCameraUri;

    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    getContentResolver().takePersistableUriPermission(uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    pendingAvatarUri = uri.toString();
                    pendingAvatarBase64 = com.example.jntt.utils.ImageUtils.uriToBase64(this, uri);
                    ImageView iv = findViewById(R.id.ivAvatarPreview);
                    if (pendingAvatarBase64 != null) {
                        com.example.jntt.utils.ImageUtils.setAvatarFromBase64(iv, pendingAvatarBase64);
                    } else {
                        iv.setImageURI(uri);
                    }
                    iv.setBackground(null);
                }
            });

    private final ActivityResultLauncher<Uri> takePicture = registerForActivityResult(
            new ActivityResultContracts.TakePicture(), success -> {
                if (success && currentCameraUri != null) {
                    pendingAvatarUri = currentCameraUri.toString();
                    pendingAvatarBase64 = com.example.jntt.utils.ImageUtils.uriToBase64(this, currentCameraUri);
                    ImageView iv = findViewById(R.id.ivAvatarPreview);
                    if (pendingAvatarBase64 != null) {
                        com.example.jntt.utils.ImageUtils.setAvatarFromBase64(iv, pendingAvatarBase64);
                    } else {
                        iv.setImageURI(currentCameraUri);
                    }
                    iv.setBackground(null);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        dm = DataManager.getInstance(this);
        username = dm.getLoggedUser();

        EditText etNickname = findViewById(R.id.etNickname);
        EditText etSignature = findViewById(R.id.etSignature);
        ImageView ivAvatar = findViewById(R.id.ivAvatarPreview);
        TextView tvBack = findViewById(R.id.tvBack);
        TextView tvSave = findViewById(R.id.tvSave);
        tvPhone = findViewById(R.id.tvPhone);
        tvBindPhoneBtn = findViewById(R.id.tvBindPhoneBtn);

        etNickname.setText(dm.getNickname(username));
        etSignature.setText(dm.getSignature(username));

        refreshPhoneDisplay();

        String existingUri = dm.getAvatarUri(username);
        if (existingUri != null) {
            try {
                if (existingUri.startsWith("data:image")) {
                    com.example.jntt.utils.ImageUtils.setAvatarFromBase64(ivAvatar, existingUri);
                } else {
                    ivAvatar.setImageURI(Uri.parse(existingUri));
                }
                ivAvatar.setBackground(null);
            } catch (Exception ignored) {
            }
        }

        tvBack.setOnClickListener(v -> finish());

        tvSave.setOnClickListener(v -> {
            String nick = etNickname.getText().toString().trim();
            String sig = etSignature.getText().toString().trim();
            if (nick.isEmpty()) {
                Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (nick.length() > 12) {
                Toast.makeText(this, "昵称不能超过12个字符", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = true;

            // 只有发生了变化，或者用户上传了新头像，才去更新那单独的一项
            if (!nick.equals(dm.getNickname(username))) {
                success &= dm.setNickname(username, nick);
            }
            if (!sig.equals(dm.getSignature(username))) {
                success &= dm.updateSignature(username, sig);
            }
            if (pendingAvatarBase64 != null) {
                success &= dm.setAvatarUri(username, pendingAvatarBase64);
            } else if (pendingAvatarUri != null) {
                success &= dm.setAvatarUri(username, pendingAvatarUri);
            }

            if (success) {
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "保存失败，该账号状态异常，请重新登录", Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.layoutAvatar).setOnClickListener(v -> showImagePickerDialog());

        tvBindPhoneBtn.setOnClickListener(v -> showBindPhoneDialog());
    }

    private Uri createImageFile() {
        File imagePath = new File(getCacheDir(), "images");
        if (!imagePath.exists())
            imagePath.mkdirs();
        File newFile = new File(imagePath, "avatar_" + System.currentTimeMillis() + ".jpg");
        return FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", newFile);
    }

    private void showImagePickerDialog() {
        com.example.jntt.utils.ImageUtils.showImagePickerDialog(this, "修改头像",
                new com.example.jntt.utils.ImageUtils.OnImagePickerListener() {
                    @Override
                    public void onTakePhoto() {
                        currentCameraUri = createImageFile();
                        takePicture.launch(currentCameraUri);
                    }

                    @Override
                    public void onPickFromGallery() {
                        pickImage.launch("image/*");
                    }
                });
    }

    private void refreshPhoneDisplay() {
        String phone = dm.getPhone(username);
        if (phone != null && !phone.isEmpty()) {
            tvPhone.setText(phone);
            tvBindPhoneBtn.setText("修改绑定");
        } else {
            tvPhone.setText("未绑定");
            tvBindPhoneBtn.setText("去绑定");
        }
    }

    private void showBindPhoneDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_bind_phone, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        EditText input = view.findViewById(R.id.etBindPhoneInput);
        view.findViewById(R.id.btnBindCancel).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btnBindConfirm).setOnClickListener(v -> {
            String phone = input.getText().toString().trim();
            if (phone.isEmpty()) {
                Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (phone.length() != 11) {
                Toast.makeText(this, "请输入11位手机号", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean ok = dm.updatePhone(username, phone);
            if (ok) {
                Toast.makeText(this, "绑定成功", Toast.LENGTH_SHORT).show();
                refreshPhoneDisplay();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "该手机号已被其他账号绑定，请更换手机号", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
}
