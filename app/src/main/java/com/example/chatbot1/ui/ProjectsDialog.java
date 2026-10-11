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

public class ProjectsDialog {

    public interface OnProjectSelectedListener {
        void onProjectPrompt(String projectPrompt);
    }

    public static void show(Context context, OnProjectSelectedListener listener) {
        if (context == null) return;

        Dialog dialog = new Dialog(context, R.style.FullScreenDialogTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_full_screen_feature, null);

        TextView tvTitle = view.findViewById(R.id.tvTitleFullScreen);
        TextView tvSubtitle = view.findViewById(R.id.tvSubtitleFullScreen);
        ImageView btnBack = view.findViewById(R.id.btnBackFullScreen);
        LinearLayout container = view.findViewById(R.id.containerFullScreenContent);

        if (tvTitle != null) tvTitle.setText("ML Projects Workspace");
        if (tvSubtitle != null) tvSubtitle.setText("Explore end-to-end Machine Learning project templates");

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> dialog.dismiss());
        }

        String[] titles = new String[]{
                "📁 Image Classification Project (CNN / Vision)",
                "📁 House Price Regression Project",
                "📁 Customer Churn Classification Project",
                "📁 Sentiment Analysis NLP Project",
                "➕ Create New ML Project Workspace"
        };

        String[] prompts = new String[]{
                "Help me design an Image Classification Project workflow using CNN.",
                "Help me build a House Price Regression Project step-by-step.",
                "Guide me on building a Customer Churn Classification Project.",
                "Guide me on building a Sentiment Analysis NLP Project.",
                "How do I structure a new Machine Learning project from dataset collection to deployment?"
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
                    listener.onProjectPrompt(prompt);
                }
            });

            container.addView(card);
        }

        dialog.setContentView(view);
        dialog.show();
    }
}