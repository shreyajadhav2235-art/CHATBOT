package com.example.chatbot1.nlp;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.chatbot1.model.ChatSession;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatHistoryRepository {

    private static final String PREF_NAME = "ml_assistant_chat_history_v2";
    private static final String KEY_SESSIONS = "chat_sessions_json";

    private final SharedPreferences prefs;
    private final List<ChatSession> sessions;

    public ChatHistoryRepository(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.sessions = new ArrayList<>();
        loadSessionsFromDisk();
    }

    private void loadSessionsFromDisk() {
        sessions.clear();
        String jsonStr = prefs.getString(KEY_SESSIONS, "[]");
        try {
            JSONArray array = new JSONArray(jsonStr);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                ChatSession session = ChatSession.fromJsonObject(obj);
                if (session != null && session.getMessages() != null && !session.getMessages().isEmpty()) {
                    sessions.add(session);
                }
            }
            sortSessionsByUpdated();
        } catch (Exception ignored) {}
    }

    public void saveSessionsToDisk() {
        try {
            sortSessionsByUpdated();
            JSONArray array = new JSONArray();
            for (ChatSession session : sessions) {
                array.put(session.toJsonObject());
            }
            prefs.edit().putString(KEY_SESSIONS, array.toString()).apply();
        } catch (Exception ignored) {}
    }

    public List<ChatSession> getSessions() {
        sortSessionsByUpdated();
        return sessions;
    }

    public ChatSession getSessionById(String id) {
        if (id == null) return null;
        for (ChatSession session : sessions) {
            if (id.equals(session.getId())) {
                return session;
            }
        }
        return null;
    }

    public void saveOrUpdateSession(ChatSession session) {
        if (session == null || session.getMessages() == null || session.getMessages().isEmpty()) return;

        session.setUpdatedAt(System.currentTimeMillis());

        int existingIndex = -1;
        for (int i = 0; i < sessions.size(); i++) {
            if (sessions.get(i).getId().equals(session.getId())) {
                existingIndex = i;
                break;
            }
        }

        if (existingIndex != -1) {
            sessions.set(existingIndex, session);
        } else {
            sessions.add(0, session);
        }

        saveSessionsToDisk();
    }

    public void deleteSession(String id) {
        if (id == null) return;
        sessions.removeIf(session -> id.equals(session.getId()));
        saveSessionsToDisk();
    }

    public void clearAllSessions() {
        sessions.clear();
        prefs.edit().remove(KEY_SESSIONS).apply();
    }

    public void renameSession(String id, String newTitle) {
        ChatSession session = getSessionById(id);
        if (session != null && newTitle != null && !newTitle.trim().isEmpty()) {
            session.setTitle(newTitle.trim());
            saveSessionsToDisk();
        }
    }

    private void sortSessionsByUpdated() {
        Collections.sort(sessions, (s1, s2) -> Long.compare(s2.getUpdatedAt(), s1.getUpdatedAt()));
    }
}