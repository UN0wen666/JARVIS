package com.psru.ginraidee.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.psru.ginraidee.R;
import com.psru.ginraidee.model.Food;
import com.psru.ginraidee.model.FoodCategory;
import com.psru.ginraidee.model.FoodFilter;
import com.psru.ginraidee.model.FoodManager;

import java.util.List;

/**
 * หน้าหมุนสุ่ม (Story Board หน้าที่ 3)
 *
 * <p>Flow ตามหลัก MVC:
 * ผู้ใช้กดปุ่ม "หมุน" (View) → Activity นี้รับ event (Controller)
 * → เรียก {@link FoodManager#randomFood(FoodCategory, FoodFilter)} (Model)
 * → ได้ object {@link Food} กลับมา → ส่งต่อให้ {@link ResultActivity} แสดงผล</p>
 */
public class RandomActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY = "extra_category";

    /** ระยะเวลาที่โชว์อนิเมชันหมุน (มิลลิวินาที) */
    private static final long SPIN_DURATION_MS = 1200;
    private static final long SPIN_FRAME_MS = 70;

    private final FoodManager foodManager = FoodManager.getInstance();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private FoodCategory category;
    private TextView tvSpinning;
    private Button btnSpin;
    private EditText etMaxPrice;
    private EditText etMaxCalories;
    private CheckBox cbVegetarian;
    private boolean spinning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);

        category = FoodCategory.fromName(getIntent().getStringExtra(EXTRA_CATEGORY));

        TextView tvCategory = findViewById(R.id.tvCategory);
        tvSpinning = findViewById(R.id.tvSpinning);
        btnSpin = findViewById(R.id.btnSpin);
        etMaxPrice = findViewById(R.id.etMaxPrice);
        etMaxCalories = findViewById(R.id.etMaxCalories);
        cbVegetarian = findViewById(R.id.cbVegetarian);

        tvCategory.setText(getString(R.string.random_category_format,
                category.getEmoji(), category.getDisplayName()));
        tvSpinning.setText(R.string.random_idle_hint);

        btnSpin.setOnClickListener(v -> startSpin());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    /** เริ่มหมุน: โชว์ชื่อเมนูสลับไปมาก่อน แล้วค่อยเฉลยผลจริง */
    private void startSpin() {
        if (spinning) {
            return;
        }

        FoodFilter filter = readFilterFromInput();
        List<Food> candidates = foodManager.findCandidates(category, filter);

        if (candidates.isEmpty()) {
            Toast.makeText(this, R.string.random_no_match, Toast.LENGTH_LONG).show();
            return;
        }

        final Food result = foodManager.randomFood(category, filter);
        if (result == null) {
            Toast.makeText(this, R.string.random_no_match, Toast.LENGTH_LONG).show();
            return;
        }

        spinning = true;
        btnSpin.setEnabled(false);
        animateSpin(candidates, result, filter);
    }

    /** สลับชื่อเมนูไปเรื่อย ๆ ให้เหมือนวงล้อกำลังหมุน แล้วเปิดหน้าผลลัพธ์ */
    private void animateSpin(List<Food> candidates, Food result, FoodFilter filter) {
        final long startTime = System.currentTimeMillis();

        Runnable frame = new Runnable() {
            @Override
            public void run() {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed < SPIN_DURATION_MS) {
                    Food preview = candidates.get((int) (elapsed / SPIN_FRAME_MS) % candidates.size());
                    tvSpinning.setText(preview.getEmoji() + "  " + preview.getFoodName());
                    handler.postDelayed(this, SPIN_FRAME_MS);
                } else {
                    spinning = false;
                    btnSpin.setEnabled(true);
                    tvSpinning.setText(R.string.random_idle_hint);
                    openResult(result, filter);
                }
            }
        };
        handler.post(frame);
    }

    private void openResult(Food result, FoodFilter filter) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(ResultActivity.EXTRA_FOOD, result);
        intent.putExtra(ResultActivity.EXTRA_CATEGORY, category.name());
        intent.putExtra(ResultActivity.EXTRA_FILTER, filter);
        startActivity(intent);
    }

    /** อ่านเงื่อนไขที่ผู้ใช้กรอกในหน้าจอ แล้วแปลงเป็น object ให้ Model ใช้ */
    private FoodFilter readFilterFromInput() {
        return new FoodFilter(
                parseNumber(etMaxPrice.getText().toString()),
                (int) parseNumber(etMaxCalories.getText().toString()),
                cbVegetarian.isChecked());
    }

    /** ช่องว่างหรือกรอกไม่ถูก ให้ถือว่า "ไม่จำกัด" (0) */
    private double parseNumber(String text) {
        try {
            return text == null || text.trim().isEmpty() ? 0 : Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
