package com.psru.ginraidee.controller;

import android.os.Handler;
import android.os.Looper;

import com.psru.ginraidee.BuildConfig;
import com.psru.ginraidee.model.AiSuggestion;
import com.psru.ginraidee.model.Food;
import com.psru.ginraidee.model.FoodManager;
import com.psru.ginraidee.util.GeminiApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller ของฟีเจอร์ "แนะนำโดย AI"
 *
 * <p>หน้าที่:</p>
 * <ol>
 *   <li>รับข้อความที่ผู้ใช้พิมพ์มาจาก View ({@link AiActivity})</li>
 *   <li>ประกอบ prompt แล้วสั่ง {@link GeminiApiClient} ยิง API บน background thread</li>
 *   <li>แปลง JSON ที่ได้กลับมาเป็น {@link AiSuggestion} และจับคู่กับเมนูในคลัง offline</li>
 *   <li>ส่งผลลัพธ์กลับขึ้น main thread ให้ View เอาไปแสดง</li>
 * </ol>
 *
 * <p>สังเกตว่าคลาสนี้ไม่รู้จัก layout หรือ widget ใด ๆ เลย มันคุยกับ View ผ่าน
 * {@link Callback} เท่านั้น — นี่คือหัวใจของการแยกชั้นแบบ MVC</p>
 */
public class AiSuggestController {

    /** ช่องทางส่งผลลัพธ์กลับไปให้หน้าจอ */
    public interface Callback {
        void onSuccess(List<AiSuggestion> suggestions);

        void onError(String message);
    }

    private static final int MAX_SUGGESTIONS = 3;

    private final FoodManager foodManager;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public AiSuggestController(FoodManager foodManager) {
        this.foodManager = foodManager;
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /** ใส่ API key ไว้ใน local.properties หรือยัง */
    public boolean isApiKeyConfigured() {
        return BuildConfig.GEMINI_API_KEY != null && !BuildConfig.GEMINI_API_KEY.trim().isEmpty();
    }

    /**
     * ขอคำแนะนำเมนูจากคำอธิบายของผู้ใช้
     * ถ้ายังไม่ได้ตั้ง API key จะเปลี่ยนไปใช้การค้นหาในเครื่องแทน แอปจะได้ยังเดโมได้
     */
    public void suggest(String userInput, Callback callback) {
        if (userInput == null || userInput.trim().isEmpty()) {
            callback.onError("ช่วยพิมพ์บอกหน่อยว่าตอนนี้อยากกินแนวไหน");
            return;
        }

        final String question = userInput.trim();

        if (!isApiKeyConfigured()) {
            List<AiSuggestion> offline = suggestOffline(question);
            if (offline.isEmpty()) {
                callback.onError("AI ยังไม่เสร็จ\n"
                        + "ฝากหน่อยเพื่อนช่วยคิดที");
            } else {
                callback.onSuccess(offline);
            }
            return;
        }

        executor.execute(() -> {
            try {
                String json = GeminiApiClient.suggestMenus(
                        BuildConfig.GEMINI_API_KEY,
                        BuildConfig.GEMINI_MODEL,
                        buildPrompt(question));
                List<AiSuggestion> suggestions = parseSuggestions(json);

                if (suggestions.isEmpty()) {
                    postError(callback, "AI ยังไม่มีเมนูแนะนำ ลองอธิบายเพิ่มอีกนิดนะ");
                } else {
                    mainHandler.post(() -> callback.onSuccess(suggestions));
                }
            } catch (Exception e) {
                // ต่อเน็ตไม่ได้ก็ยังพอช่วยผู้ใช้ได้ด้วยการค้นในคลัง offline
                List<AiSuggestion> offline = suggestOffline(question);
                if (offline.isEmpty()) {
                    postError(callback, "เชื่อมต่อ AI ไม่สำเร็จ: " + e.getMessage());
                } else {
                    mainHandler.post(() -> callback.onSuccess(offline));
                }
            }
        });
    }

    /** ปิด thread pool ตอนหน้าจอถูกทำลาย ไม่งั้น thread ค้าง */
    public void shutdown() {
        executor.shutdown();
    }

    // ------------------------------------------------------------------
    // ส่วนประกอบภายใน
    // ------------------------------------------------------------------

    /**
     * ประกอบ prompt โดยแนบรายชื่อเมนูในเครื่องไปด้วย
     * เพื่อให้ AI เลือกจากเมนูที่เรามีจริงก่อน ผลลัพธ์จะได้จับคู่กับข้อมูลจริงได้
     */
    private String buildPrompt(String userInput) {
        StringBuilder menuNames = new StringBuilder();
        for (Food food : foodManager.getFoodList()) {
            if (menuNames.length() > 0) {
                menuNames.append(", ");
            }
            menuNames.append(food.getFoodName());
        }

        return "คุณเป็นผู้ช่วยเลือกเมนูอาหารของแอป \"Gin Rai Dee?\" ตอบเป็นภาษาไทยเสมอ\n\n"
                + "รายการเมนูที่แอปมีอยู่: " + menuNames + "\n\n"
                + "สิ่งที่ผู้ใช้อยากกิน: \"" + userInput + "\"\n\n"
                + "ให้แนะนำเมนู " + MAX_SUGGESTIONS + " อย่าง เรียงจากที่เหมาะที่สุด\n"
                + "- พยายามเลือกจากรายการเมนูข้างบนก่อน ถ้าไม่มีอันไหนเข้ากันจริง ๆ ค่อยเสนอเมนูอื่น\n"
                + "- reason ให้เขียนสั้น ๆ ไม่เกิน 2 บรรทัด บอกว่าทำไมเมนูนี้ถึงเหมาะกับที่ผู้ใช้บอก";
    }

    /** แปลง JSON array ที่ Gemini ตอบกลับมาเป็น list ของ object */
    private List<AiSuggestion> parseSuggestions(String json) throws Exception {
        List<AiSuggestion> suggestions = new ArrayList<>();
        if (json == null || json.trim().isEmpty()) {
            return suggestions;
        }

        JSONArray array = new JSONArray(json.trim());
        for (int i = 0; i < array.length() && suggestions.size() < MAX_SUGGESTIONS; i++) {
            JSONObject item = array.getJSONObject(i);
            String name = item.optString("foodName", "").trim();
            String reason = item.optString("reason", "").trim();
            if (name.isEmpty()) {
                continue;
            }
            // จับคู่กับคลัง offline เพื่อเอาราคา/แคลอรี่จริงมาแสดง
            suggestions.add(new AiSuggestion(name, reason, foodManager.findByName(name)));
        }
        return suggestions;
    }

    /**
     * แผนสำรองเมื่อไม่มี API key หรือเน็ตล่ม — ค้นจากคลังเมนูในเครื่องด้วยคำที่ผู้ใช้พิมพ์
     * ใช้ FoodManager.searchFood() ที่เขียนไว้แล้ว ไม่ต้องเขียนตรรกะซ้ำ
     */
    private List<AiSuggestion> suggestOffline(String userInput) {
        List<AiSuggestion> suggestions = new ArrayList<>();
        for (String keyword : userInput.split("[\\s,]+")) {
            if (keyword.length() < 2) {
                continue;
            }
            for (Food food : foodManager.searchFood(keyword)) {
                if (suggestions.size() >= MAX_SUGGESTIONS) {
                    return suggestions;
                }
                if (!containsFood(suggestions, food)) {
                    suggestions.add(new AiSuggestion(food.getFoodName(),
                            "ค้นเจอในเมนูของแอปจากคำว่า \"" + keyword + "\" (โหมดออฟไลน์)", food));
                }
            }
        }
        return suggestions;
    }

    private boolean containsFood(List<AiSuggestion> suggestions, Food food) {
        for (AiSuggestion suggestion : suggestions) {
            if (suggestion.getFoodName().equals(food.getFoodName())) {
                return true;
            }
        }
        return false;
    }

    private void postError(Callback callback, String message) {
        mainHandler.post(() -> callback.onError(message));
    }
}
