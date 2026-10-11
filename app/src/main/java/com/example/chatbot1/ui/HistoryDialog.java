package com.example.chatbot1.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.chatbot1.R;
import com.example.chatbot1.model.ChatSession;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class HistoryDialog {

    public interface OnHistoryActionListener {
        void onSelectSession(ChatSession session);
        void onDeleteSession(ChatSession session);
        void onClearAllHistory();
    }

    public static void show(Context context, List<ChatSession> sessions, OnHistoryActionListener listener) {
        if (context == null) return;

        Dialog dialog = new Dialog(context, R.style.FullScreenDialogTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_full_screen_feature, null);

        TextView tvTitle = view.findViewById(R.id.tvTitleFullScreen);
        TextView tvSubtitle = view.findViewById(R.id.tvSubtitleFullScreen);
        ImageView btnBack = view.findViewById(R.id.btnBackFullScreen);
        LinearLayout container = view.findViewById(R.id.containerFullScreenContent);

        if (tvTitle != null) tvTitle.setText("Saved Chat History");
        if (tvSubtitle != null) tvSubtitle.setText("Select a conversation to load and continue");

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> dialog.dismiss());
        }

        if (sessions == null || sessions.isEmpty()) {
            TextView emptyText = new TextView(context);
            emptyText.setText("No saved chat history conversations found.\n\nStart asking Machine Learning questions to save history!");
            emptyText.setTextColor(context.getColor(R.color.text_secondary));
            emptyText.setTextSize(15);
            emptyText.setPadding(32, 64, 32, 64);
            container.addView(emptyText);
        } else {
            for (ChatSession session : sessions) {
                MaterialCardView card = new MaterialCardView(context);
                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                cardParams.setMargins(0, 0, 0, 16);
                card.setLayoutParams(cardParams);
                card.setCardBackgroundColor(context.getColor(R.color.bot_bubble_bg));
                card.setStrokeColor(context.getColor(R.color.border_color));
                card.setStrokeWidth(2);
                card.setRadius(24);

                LinearLayout itemLayout = new LinearLayout(context);
                itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                itemLayout.setPadding(24, 20, 24, 20);

                TextView tvSessionTitle = new TextView(context);
                tvSessionTitle.setText("💬 " + session.getTitle());
                tvSessionTitle.setTextColor(context.getColor(R.color.text_primary));
                tvSessionTitle.setTextSize(15);
                tvSessionTitle.setTypeface(null, android.graphics.Typeface.BOLD);

                LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                tvSessionTitle.setLayoutParams(titleParams);

                ImageView btnDelete = new ImageView(context);
                btnDelete.setImageResource(R.drawable.ic_close);
                btnDelete.setColorFilter(context.getColor(R.color.text_secondary));
                btnDelete.setPadding(12, 12, 12, 12);

                itemLayout.addView(tvSessionTitle);
                itemLayout.addView(btnDelete);
                card.addView(itemLayout);

                card.setOnClickListener(v -> {
                    dialog.dismiss();
                    if (listener != null) {
                        listener.onSelectSession(session);
                    }
                });

                btnDelete.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeleteSession(session);
                    }
                    dialog.dismiss();
                });

                container.addView(card);
            }

            // Clear All History Button
            MaterialCardView clearCard = new MaterialCardView(context);
            LinearLayout.LayoutParams clearParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            clearParams.setMargins(0, 16, 0, 32);
            clearCard.setLayoutParams(clearParams);
            clearCard.setCardBackgroundColor(context.getColor(R.color.user_bubble_bg));
            clearCard.setRadius(24);

            TextView tvClear = new TextView(context);
            tvClear.setText("🧹 Clear All Chat History");
            tvClear.setTextColor(context.getColor(R.color.text_primary));
            tvClear.setTextSize(15);
            tvClear.setTypeface(null, android.graphics.Typeface.BOLD);
            tvClear.setPadding(32, 24, 32, 24);
            tvClear.setGravity(android.view.Gravity.CENTER);

            clearCard.addView(tvClear);
            clearCard.setOnClickListener(v -> {
                dialog.dismiss();
                if (listener != null) {
                    listener.onClearAllHistory();
                }
            });

            container.addView(clearCard);
        }

        dialog.setContentView(view);
        dialog.show();
    }
}