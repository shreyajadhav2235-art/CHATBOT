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

public class TasksDialog {

    public interface OnTaskSelectedListener {
        void onTaskPrompt(String taskPrompt);
    }

    public static void show(Context context, OnTaskSelectedListener listener) {
        if (context == null) return;

        Dialog dialog = new Dialog(context, R.style.FullScreenDialogTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_full_screen_feature, null);

        TextView tvTitle = view.findViewById(R.id.tvTitleFullScreen);
        TextView tvSubtitle = view.findViewById(R.id.tvSubtitleFullScreen);
        ImageView btnBack = view.findViewById(R.id.btnBackFullScreen);
        LinearLayout container = view.findViewById(R.id.containerFullScreenContent);

        if (tvTitle != null) tvTitle.setText("ML Study Tasks & Reminders");
        if (tvSubtitle != null) tvSubtitle.setText("Track and practice Machine Learning study goals");

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> dialog.dismiss());
        }

        String[] titles = new String[]{
                "✅ Review Supervised vs Unsupervised Concepts",
                "⏰ Practice Decision Tree & Random Forest Questions",
                "📝 Solve SVM & Kernel Trick Math Equations",
                "💻 Write Scikit-Learn Model Evaluation Script",
                "➕ Add New ML Study Task"
        };

        String[] prompts = new String[]{
                "Help me review Supervised vs Unsupervised Learning concepts for my study task.",
                "Give me practice questions on Decision Tree and Random Forest.",
                "Explain SVM hyperplane and margin equations for my math task.",
                "Give me a template script to evaluate model accuracy, precision, recall, and F1 score.",
                "Create a custom 7-day Machine Learning study schedule for me."
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
                    listener.onTaskPrompt(prompt);
                }
            });

            container.addView(card);
        }

        dialog.setContentView(view);
        dialog.show();
    }
}