package com.psru.ginraidee.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.psru.ginraidee.R;
import com.psru.ginraidee.model.AiSuggestion;
import com.psru.ginraidee.model.Food;

import java.util.ArrayList;
import java.util.List;

/**
 * ตัวเชื่อมระหว่างข้อมูล {@link AiSuggestion} กับ layout {@code item_food.xml}
 *
 * <p>อยู่ในชั้น Controller เพราะหน้าที่ของมันคือ "เอาข้อมูลจาก Model ไปใส่ View"
 * ตัวมันเองไม่ได้เก็บกฎทางธุรกิจอะไรเลย</p>
 */
public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder> {

    private final List<AiSuggestion> suggestions = new ArrayList<>();

    /** แทนที่ข้อมูลทั้งชุดแล้วสั่งวาดใหม่ */
    public void submit(List<AiSuggestion> newSuggestions) {
        suggestions.clear();
        if (newSuggestions != null) {
            suggestions.addAll(newSuggestions);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        holder.bind(suggestions.get(position));
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    static class SuggestionViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvEmoji;
        private final TextView tvName;
        private final TextView tvDetail;

        SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tvItemEmoji);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvDetail = itemView.findViewById(R.id.tvItemDetail);
        }

        void bind(AiSuggestion suggestion) {
            tvEmoji.setText(suggestion.getEmoji());
            tvName.setText(suggestion.getFoodName());

            StringBuilder detail = new StringBuilder(suggestion.getReason());
            Food matched = suggestion.getMatchedFood();
            if (matched != null) {
                // จับคู่กับคลังเมนู offline ได้ เลยเอาข้อมูลจริงมาต่อท้าย
                detail.append("\n")
                        .append(matched.getCalories())
                        .append(" แคลอรี่ • ")
                        .append(matched.getTag());
            }
            tvDetail.setText(detail.toString());
        }
    }
}
