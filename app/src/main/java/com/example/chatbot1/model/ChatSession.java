package com.example.chatbot1.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatSession {

    private String id;
    private String title;
    private List<ChatMessage> messages;
    private long createdAt;
    private long updatedAt;

    public ChatSession() {
        this.id = UUID.randomUUID().toString();
        this.title = "New Conversation";
        this.messages = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public ChatSession(String id, String title, List<ChatMessage> messages, long createdAt, long updatedAt) {
        this.id = id;
        this.title = title;
        this.messages = messages != null ? messages : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void addMessage(ChatMessage message) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(message);
        this.updatedAt = System.currentTimeMillis();
    }

    public JSONObject toJsonObject() {
        try {
            JSONObject json = new JSONObject();
            json.put("id", id);
            json.put("title", title);
            json.put("createdAt", createdAt);
            json.put("updatedAt", updatedAt);

            JSONArray msgArray = new JSONArray();
            if (messages != null) {
                for (ChatMessage msg : messages) {
                    if (msg.getViewType() == ChatMessage.TYPE_USER || msg.getViewType() == ChatMessage.TYPE_BOT) {
                        JSONObject m = new JSONObject();
                        m.put("viewType", msg.getViewType());
                        m.put("text", msg.getText());
                        m.put("timestamp", msg.getTimestamp() != null ? msg.getTimestamp() : "");
                        m.put("answerLevel", msg.getAnswerLevel() != null ? msg.getAnswerLevel() : "");
                        m.put("topicTitle", msg.getTopicTitle() != null ? msg.getTopicTitle() : "");
                        msgArray.put(m);
                    }
                }
            }
            json.put("messages", msgArray);
            return json;
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public static ChatSession fromJsonObject(JSONObject json) {
        try {
            String id = json.optString("id", UUID.randomUUID().toString());
            String title = json.optString("title", "Conversation");
            long createdAt = json.optLong("createdAt", System.currentTimeMillis());
            long updatedAt = json.optLong("updatedAt", System.currentTimeMillis());

            List<ChatMessage> msgs = new ArrayList<>();
            JSONArray msgArray = json.optJSONArray("messages");
            if (msgArray != null) {
                for (int i = 0; i < msgArray.length(); i++) {
                    JSONObject m = msgArray.getJSONObject(i);
                    int viewType = m.optInt("viewType", ChatMessage.TYPE_BOT);
                    String text = m.optString("text", "");
                    String timestamp = m.optString("timestamp", "");
                    String answerLevel = m.optString("answerLevel", "");
                    String topicTitle = m.optString("topicTitle", "");

                    ChatMessage msg = new ChatMessage(viewType, text, timestamp, answerLevel, topicTitle);
                    msgs.add(msg);
                }
            }

            return new ChatSession(id, title, msgs, createdAt, updatedAt);
        } catch (Exception e) {
            return new ChatSession();
        }
    }
}