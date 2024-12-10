package com.example.mygamedemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);

        // Thiết lập WindowInsets cho layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Tham chiếu tới nút Logout
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button logoutButton = findViewById(R.id.btn_logout);

        // Gắn sự kiện nhấn nút Logout
        logoutButton.setOnClickListener(v -> {
            // Ví dụ: Chuyển người dùng về LoginActivity
            Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Đóng SettingActivity
        });
    }
}
