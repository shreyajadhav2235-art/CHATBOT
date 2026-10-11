package com.example.chatbot1.model;

import java.util.ArrayList;
import java.util.List;

public class ChatMessage {

    public static final int TYPE_USER = 1;
    public static final int TYPE_BOT = 2;
    public static final int TYPE_WELCOME = 3;
    public static final int TYPE_TYPING = 4;

    private int viewType;
    private String text;
    private String timestamp;
    private String answerLevel; // e.g., "QUICK", "NORMAL", "DEEP", "COMPARISON"
    private String topicTitle;
    private List<String> suggestedQuestions;

    public ChatMessage(int viewType, String text, String timestamp) {
        this.viewType = viewType;
        this.text = text;
        this.timestamp = timestamp;
        this.suggestedQuestions = new ArrayList<>();
    }

    public ChatMessage(int viewType, String text, String timestamp, String answerLevel, String topicTitle) {
        this.viewType = viewType;
        this.text = text;
        this.timestamp = timestamp;
        this.answerLevel = answerLevel;
        this.topicTitle = topicTitle;
        this.suggestedQuestions = new ArrayList<>();
    }

    public int getViewType() {
        return viewType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getAnswerLevel() {
        return answerLevel;
    }

    public void setAnswerLevel(String answerLevel) {
        this.answerLevel = answerLevel;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public List<String> getSuggestedQuestions() {
        return suggestedQuestions;
    }

    public void setSuggestedQuestions(List<String> suggestedQuestions) {
        this.suggestedQuestions = suggestedQuestions;
    }
}