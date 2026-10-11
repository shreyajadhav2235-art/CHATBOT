package com.example.chatbot1.multimodal;

import android.graphics.Bitmap;
import android.net.Uri;

public class Attachment {

    public enum Type {
        IMAGE,
        PDF,
        DOCUMENT,
        AUDIO
    }

    private Uri uri;
    private Type type;
    private String fileName;
    private long fileSize;
    private Bitmap thumbnail;
    private String extractedText;

    public Attachment(Uri uri, Type type, String fileName, long fileSize) {
        this.uri = uri;
        this.type = type;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public Uri getUri() {
        return uri;
    }

    public Type getType() {
        return type;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getFormattedSize() {
        if (fileSize <= 0) return "0 KB";
        if (fileSize < 1024 * 1024) {
            return (fileSize / 1024) + " KB";
        }
        return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }

    public boolean hasExtractedText() {
        return extractedText != null && !extractedText.trim().isEmpty();
    }
}