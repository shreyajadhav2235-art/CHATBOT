package com.example.chatbot1.nlp;

import com.example.chatbot1.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ConversationManager {

    private String lastTopicId;
    private String lastTopicTitle;
    private IntentClassifier.Intent lastIntent;
    private String lastAnswerLevel;
    private final List<ChatMessage> history;

    public ConversationManager() {
        this.history = new ArrayList<>();
        reset();
    }

    public void reset() {
        this.lastTopicId = null;
        this.lastTopicTitle = null;
        this.lastIntent = null;
        this.lastAnswerLevel = "NORMAL";
        this.history.clear();
    }

    public void addMessage(ChatMessage message) {
        this.history.add(message);
    }

    public List<ChatMessage> getHistory() {
        return history;
    }

    public String getLastTopicId() {
        return lastTopicId;
    }

    public void updateContext(String topicId, String topicTitle, IntentClassifier.Intent intent, String answerLevel) {
        if (topicId != null) {
            this.lastTopicId = topicId;
            this.lastTopicTitle = topicTitle;
        }
        if (intent != null) {
            this.lastIntent = intent;
        }
        if (answerLevel != null) {
            this.lastAnswerLevel = answerLevel;
        }
    }

    public String getLastTopicTitle() {
        return lastTopicTitle;
    }

    public IntentClassifier.Intent getLastIntent() {
        return lastIntent;
    }

    public String getLastAnswerLevel() {
        return lastAnswerLevel;
    }

    public boolean hasActiveTopic() {
        return lastTopicId != null;
    }
}