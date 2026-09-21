package com.psru.ginraidee.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.psru.ginraidee.R;
import com.psru.ginraidee.model.FoodManager;

/**
 * หน้าแรกของแอป (Story Board หน้าที่ 1)
 *
 * <p>บทบาทในแบบ MVC: เป็น <b>Controller</b> — รับ event การกดปุ่มจาก View (XML)
 * แล้วสั่งเปลี่ยนหน้า ไม่มีตรรกะการสุ่มอยู่ในนี้เลย</p>
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvMenuCount = findViewById(R.id.tvMenuCount);
        Button btnStart = findViewById(R.id.btnStart);
        Button btnAi = findViewById(R.id.btnAi);

        // ถาม Model ว่าตอนนี้มีเมนูในคลังกี่จาน แล้วให้ View แสดง
        int menuCount = FoodManager.getInstance().size();
        tvMenuCount.setText(getString(R.string.main_menu_count, menuCount));

        btnStart.setOnClickListener(v ->
                startActivity(new Intent(this, ChoiceActivity.class)));

        btnAi.setOnClickListener(v ->
                startActivity(new Intent(this, AiActivity.class)));
    }
}
