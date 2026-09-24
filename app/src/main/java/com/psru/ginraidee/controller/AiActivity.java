package com.psru.ginraidee.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.psru.ginraidee.R;
import com.psru.ginraidee.model.AiSuggestion;
import com.psru.ginraidee.model.FoodManager;

import java.util.List;

/**
 * หน้าแนะนำโดย AI
 * <p>หน้าจอนี้ไม่ได้ยิง API เอง — มันแค่เก็บข้อความจากผู้ใช้ส่งให้
 * {@link AiSuggestController} แล้วรอฟังผลผ่าน callback
 * ทำให้ตรรกะการต่อเน็ตทั้งหมดอยู่นอกไฟล์นี้</p>
 */
public class AiActivity extends AppCompatActivity {

    private AiSuggestController controller;
    private SuggestionAdapter adapter;

    private EditText etPrompt;
    private Button btnAsk;
    private ProgressBar progressBar;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);

        controller = new AiSuggestController(FoodManager.getInstance());
        adapter = new SuggestionAdapter();

        etPrompt = findViewById(R.id.etPrompt);
        btnAsk = findViewById(R.id.btnAsk);
        progressBar = findViewById(R.id.progressBar);
        tvStatus = findViewById(R.id.tvStatus);

        RecyclerView rvSuggestions = findViewById(R.id.rvSuggestions);
        rvSuggestions.setLayoutManager(new LinearLayoutManager(this));
        rvSuggestions.setAdapter(adapter);

        if (!controller.isApiKeyConfigured()) {
            tvStatus.setText(R.string.ai_no_key_hint);
        } else {
            tvStatus.setText(R.string.ai_idle_hint);
        }

        btnAsk.setOnClickListener(v -> ask());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        controller.shutdown();
        super.onDestroy();
    }

    private void ask() {
        setLoading(true);
        adapter.submit(null);

        controller.suggest(etPrompt.getText().toString(), new AiSuggestController.Callback() {
            @Override
            public void onSuccess(List<AiSuggestion> suggestions) {
                setLoading(false);
                tvStatus.setText(getString(R.string.ai_result_count, suggestions.size()));
                adapter.submit(suggestions);
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                tvStatus.setText(message);
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnAsk.setEnabled(!loading);
        if (loading) {
            tvStatus.setText(R.string.ai_loading);
        }
    }
}
