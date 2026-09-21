package com.psru.ginraidee.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.psru.ginraidee.R;
import com.psru.ginraidee.model.Food;
import com.psru.ginraidee.model.FoodCategory;
import com.psru.ginraidee.model.FoodFilter;
import com.psru.ginraidee.model.FoodManager;

/**
 * หน้าแสดงผลลัพธ์ (Story Board หน้าที่ 4)
 *
 * <p>จุดที่เห็น Polymorphism ชัดที่สุดอยู่ในเมธอด {@link #showFood(Food)} —
 * เราเรียก {@code food.getInfo()} เหมือนกันทุกครั้ง
 * แต่ข้อความที่ออกมาต่างกันตามว่า object จริง ๆ เป็นคลาสลูกตัวไหน
 * โดยที่ Activity นี้ไม่ต้องรู้เลยว่ามันคือคลาสอะไร</p>
 */
public class ResultActivity extends AppCompatActivity {

    public static final String EXTRA_FOOD = "extra_food";
    public static final String EXTRA_CATEGORY = "extra_category";
    public static final String EXTRA_FILTER = "extra_filter";

    private final FoodManager foodManager = FoodManager.getInstance();

    private TextView tvEmoji;
    private TextView tvFoodName;
    private TextView tvTag;
    private TextView tvInfo;

    private FoodCategory category;
    private FoodFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvEmoji = findViewById(R.id.tvEmoji);
        tvFoodName = findViewById(R.id.tvFoodName);
        tvTag = findViewById(R.id.tvTag);
        tvInfo = findViewById(R.id.tvInfo);

        category = FoodCategory.fromName(getIntent().getStringExtra(EXTRA_CATEGORY));
        filter = readFilter();

        Food food = readFood();
        if (food == null) {
            // กันเหนียว เผื่อเปิดหน้านี้มาโดยไม่มีข้อมูลติดมาด้วย
            food = foodManager.randomFood(category, filter);
        }
        showFood(food);

        Button btnSpinAgain = findViewById(R.id.btnSpinAgain);
        btnSpinAgain.setOnClickListener(v -> spinAgain());

        findViewById(R.id.btnHome).setOnClickListener(v -> goHome());
    }

    /** เอา object Food มาแสดงบนหน้าจอ */
    private void showFood(Food food) {
        if (food == null) {
            tvEmoji.setText("😅");
            tvFoodName.setText(R.string.result_empty_title);
            tvTag.setText("");
            tvInfo.setText(R.string.random_no_match);
            return;
        }
        tvEmoji.setText(food.getEmoji());
        tvFoodName.setText(food.getFoodName());
        tvTag.setText(food.getTag());
        tvInfo.setText(food.getInfo());
    }

    /** ปุ่ม "หมุนใหม่" — สั่ง Model สุ่มใหม่ในหมวดและเงื่อนไขเดิม */
    private void spinAgain() {
        Food next = foodManager.randomFood(category, filter);
        if (next == null) {
            Toast.makeText(this, R.string.random_no_match, Toast.LENGTH_LONG).show();
            return;
        }
        showFood(next);
    }

    /** กลับไปหน้าแรก โดยเคลียร์หน้าที่ค้างอยู่ใน stack ทิ้ง */
    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @SuppressWarnings("deprecation")
    private Food readFood() {
        Object extra = getIntent().getSerializableExtra(EXTRA_FOOD);
        return extra instanceof Food ? (Food) extra : null;
    }

    @SuppressWarnings("deprecation")
    private FoodFilter readFilter() {
        Object extra = getIntent().getSerializableExtra(EXTRA_FILTER);
        return extra instanceof FoodFilter ? (FoodFilter) extra : FoodFilter.NONE;
    }
}
