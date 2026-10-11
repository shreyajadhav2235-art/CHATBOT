package com.example.chatbot1.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chatbot1.R;
import com.google.android.material.card.MaterialCardView;

public class LibraryDialog {

    public interface OnLibrarySelectedListener {
        void onLibraryPrompt(String libraryPrompt);
    }

    public static void show(Context context, OnLibrarySelectedListener listener) {
        if (context == null) return;

        Dialog dialog = new Dialog(context, R.style.FullScreenDialogTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_full_screen_feature, null);

        TextView tvTitle = view.findViewById(R.id.tvTitleFullScreen);
        TextView tvSubtitle = view.findViewById(R.id.tvSubtitleFullScreen);
        ImageView btnBack = view.findViewById(R.id.btnBackFullScreen);
        LinearLayout container = view.findViewById(R.id.containerFullScreenContent);

        if (tvTitle != null) tvTitle.setText("ML Learning Library");
        if (tvSubtitle != null) tvSubtitle.setText("Explore saved study notes and cheat sheets");

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> dialog.dismiss());
        }

        String[] titles = new String[]{
                "📚 ML Cheat Sheet: Algorithms Summary",
                "📊 Evaluation Metrics Guide (Precision, Recall, AUC)",
                "📖 Feature Preprocessing & Scaling Rules",
                "📐 Loss Functions & Gradient Descent Guide",
                "🔍 Search Saved ML Notes"
        };

        String[] prompts = new String[]{
                "Give me a quick ML Cheat Sheet summarizing key algorithms.",
                "Give me a detailed Evaluation Metrics Guide explaining Precision, Recall, F1 Score, and ROC-AUC.",
                "Explain Normalization vs Standardization rules from my library notes.",
                "Explain Loss Functions and Gradient Descent optimization.",
                "Search library: What are the main types of Machine Learning?"
        };

        for (int i = 0; i < titles.length; i++) {
            final String prompt = prompts[i];

            MaterialCardView card = new MaterialCardView(context);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(0, 0, 0, 16);
            card.setLayoutParams(cardParams);
            card.setCardBackgroundColor(context.getColor(R.color.bot_bubble_bg));
            card.setStrokeColor(context.getColor(R.color.border_color));
            card.setStrokeWidth(2);
            card.setRadius(24);

            TextView tvItem = new TextView(context);
            tvItem.setText(titles[i]);
            tvItem.setTextColor(context.getColor(R.color.text_primary));
            tvItem.setTextSize(15);
            tvItem.setTypeface(null, Typeface.BOLD);
            tvItem.setPadding(32, 28, 32, 28);

            card.addView(tvItem);
            card.setOnClickListener(v -> {
                dialog.dismiss();
                if (listener != null) {
                    listener.onLibraryPrompt(prompt);
                }
            });

            container.addView(card);
        }

        dialog.setContentView(view);
        dialog.show();
    }
}