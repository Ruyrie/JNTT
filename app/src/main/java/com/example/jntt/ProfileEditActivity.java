package com.example.jntt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jntt.data.DataManager;

public class ProfileEditActivity extends AppCompatActivity {

    private DataManager dm;
    private String username;
    private String pendingAvatarUri;

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        // Persist read permission
                        getContentResolver().takePersistableUriPermission(uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        pendingAvatarUri = uri.toString();
                        ImageView iv = findViewById(R.id.ivAvatarPreview);
                        iv.setImageURI(uri);
                        iv.setBackground(null);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        dm = DataManager.getInstance(this);
        username = dm.getLoggedUser();

        EditText etNickname = findViewById(R.id.etNickname);
        ImageView ivAvatar  = findViewById(R.id.ivAvatarPreview);
        TextView tvBack     = findViewById(R.id.tvBack);
        TextView tvSave     = findViewById(R.id.tvSave);

        etNickname.setText(dm.getNickname(username));

        String existingUri = dm.getAvatarUri(username);
        if (existingUri != null) {
            try {
                ivAvatar.setImageURI(Uri.parse(existingUri));
                ivAvatar.setBackground(null);
            } catch (Exception ignored) {}
        }

        tvBack.setOnClickListener(v -> finish());

        tvSave.setOnClickListener(v -> {
            String nick = etNickname.getText().toString().trim();
            if (nick.isEmpty()) { Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show(); return; }
            dm.setNickname(username, nick);
            if (pendingAvatarUri != null) dm.setAvatarUri(username, pendingAvatarUri);
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            finish();
        });

        findViewById(R.id.layoutAvatar).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            pickImage.launch(intent);
        });
    }
}
