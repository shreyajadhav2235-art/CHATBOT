package com.example.chatbot1.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatbot1.R;
import com.example.chatbot1.model.ChatMessage;
import com.example.chatbot1.utils.Utils;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnSuggestionClickListener {
        void onSuggestionClick(String suggestion);
    }

    private final Context context;
    private final List<ChatMessage> messages;
    private final OnSuggestionClickListener suggestionClickListener;

    public ChatAdapter(Context context, List<ChatMessage> messages, OnSuggestionClickListener listener) {
        this.context = context;
        this.messages = messages;
        this.suggestionClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == ChatMessage.TYPE_WELCOME) {
            View view = inflater.inflate(R.layout.item_welcome_card, parent, false);
            return new WelcomeViewHolder(view);
        } else if (viewType == ChatMessage.TYPE_USER) {
            View view = inflater.inflate(R.layout.item_user_message, parent, false);
            return new UserViewHolder(view);
        } else if (viewType == ChatMessage.TYPE_BOT) {
            View view = inflater.inflate(R.layout.item_bot_message, parent, false);
            return new BotViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_typing_indicator, parent, false);
            return new TypingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage msg = messages.get(position);

        if (holder instanceof WelcomeViewHolder) {
            ((WelcomeViewHolder) holder).bind();
        } else if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).bind(msg);
        } else if (holder instanceof BotViewHolder) {
            ((BotViewHolder) holder).bind(msg, context);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // 1. Welcome ViewHolder
    static class WelcomeViewHolder extends RecyclerView.ViewHolder {
        private final ChipGroup cgWelcomeSuggestions;

        public WelcomeViewHolder(@NonNull View itemView) {
            super(itemView);
            cgWelcomeSuggestions = itemView.findViewById(R.id.cgWelcomeSuggestions);
        }

        public void bind() {
            if (cgWelcomeSuggestions != null) {
                cgWelcomeSuggestions.removeAllViews();
                cgWelcomeSuggestions.setVisibility(View.GONE);
            }
        }
    }

    // 2. User Message ViewHolder
    static class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUserMessage;
        private final TextView tvUserTimestamp;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserMessage = itemView.findViewById(R.id.tvUserMessage);
            tvUserTimestamp = itemView.findViewById(R.id.tvUserTimestamp);
        }

        public void bind(ChatMessage message) {
            tvUserMessage.setText(message.getText());
            tvUserTimestamp.setText(message.getTimestamp());
        }
    }

    // 3. Bot Message ViewHolder
    static class BotViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvBotMessage;
        private final TextView tvBotTimestamp;
        private final TextView tvAnswerBadge;
        private final ImageView btnCopy;
        private final ChipGroup cgFollowUp;

        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBotMessage = itemView.findViewById(R.id.tvBotMessage);
            tvBotTimestamp = itemView.findViewById(R.id.tvBotTimestamp);
            tvAnswerBadge = itemView.findViewById(R.id.tvAnswerBadge);
            btnCopy = itemView.findViewById(R.id.btnCopy);
            cgFollowUp = itemView.findViewById(R.id.cgFollowUp);
        }

        public void bind(ChatMessage message, Context context) {
            // Format bold markdown (**text**)
            CharSequence formattedText = parseMarkdownBold(message.getText());
            tvBotMessage.setText(formattedText);
            tvBotTimestamp.setText(message.getTimestamp());

            // Set badge level
            String level = message.getAnswerLevel();
            if (level != null) {
                tvAnswerBadge.setVisibility(View.VISIBLE);
                switch (level) {
                    case "QUICK":
                        tvAnswerBadge.setText(R.string.badge_quick);
                        tvAnswerBadge.setBackgroundResource(R.drawable.bg_badge_quick);
                        break;
                    case "DEEP":
                        tvAnswerBadge.setText(R.string.badge_deep);
                        tvAnswerBadge.setBackgroundResource(R.drawable.bg_badge_deep);
                        break;
                    case "COMPARISON":
                        tvAnswerBadge.setText(R.string.badge_comparison);
                        tvAnswerBadge.setBackgroundResource(R.drawable.bg_badge_quick);
                        break;
                    default:
                        tvAnswerBadge.setText(R.string.badge_normal);
                        tvAnswerBadge.setBackgroundResource(R.drawable.bg_badge_quick);
                        break;
                }
            } else {
                tvAnswerBadge.setVisibility(View.GONE);
            }

            // Copy button
            btnCopy.setOnClickListener(v -> Utils.copyToClipboard(context, message.getText()));

            // Hide follow-up chips to prevent user confusion
            if (cgFollowUp != null) {
                cgFollowUp.removeAllViews();
                cgFollowUp.setVisibility(View.GONE);
            }
        }

        private CharSequence parseMarkdownBold(String rawText) {
            if (rawText == null) return "";
            SpannableStringBuilder builder = new SpannableStringBuilder();
            String[] lines = rawText.split("\n");

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];

                // Simple bold parser for **bold**
                SpannableStringBuilder lineBuilder = new SpannableStringBuilder();
                int lastIdx = 0;
                while (lastIdx < line.length()) {
                    int start = line.indexOf("**", lastIdx);
                    if (start == -1) {
                        lineBuilder.append(line.substring(lastIdx));
                        break;
                    }
                    lineBuilder.append(line.substring(lastIdx, start));
                    int end = line.indexOf("**", start + 2);
                    if (end == -1) {
                        lineBuilder.append(line.substring(start));
                        break;
                    }
                    String boldText = line.substring(start + 2, end);
                    int spanStart = lineBuilder.length();
                    lineBuilder.append(boldText);
                    lineBuilder.setSpan(new StyleSpan(Typeface.BOLD), spanStart, lineBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    lastIdx = end + 2;
                }

                builder.append(lineBuilder);
                if (i < lines.length - 1) {
                    builder.append("\n");
                }
            }

            return builder;
        }
    }

    // 4. Typing ViewHolder
    static class TypingViewHolder extends RecyclerView.ViewHolder {
        public TypingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}