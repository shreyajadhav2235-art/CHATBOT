package com.example.chatbot1.multimodal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechManager {

    public interface OnSpeechResultListener {
        void onReadyForSpeech();
        void onBeginningOfSpeech();
        void onRmsChanged(float rmsdB);
        void onResult(String text);
        void onError(String errorMessage);
    }

    private SpeechRecognizer speechRecognizer;
    private boolean isListening = false;

    public boolean isListening() {
        return isListening;
    }

    public void startListening(Context context, String language, OnSpeechResultListener listener) {
        if (context == null) return;

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            if (listener != null) listener.onError("Speech recognition is not available on this device");
            return;
        }

        stopListening();

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        if ("mr".equalsIgnoreCase(language) || "mr-IN".equalsIgnoreCase(language)) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "mr-IN");
        } else {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().getLanguage());
        }

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                isListening = true;
                if (listener != null) listener.onReadyForSpeech();
            }

            @Override
            public void onBeginningOfSpeech() {
                if (listener != null) listener.onBeginningOfSpeech();
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                if (listener != null) listener.onRmsChanged(rmsdB);
            }

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {
                isListening = false;
            }

            @Override
            public void onError(int error) {
                isListening = false;
                String errorMsg = getErrorMessage(error);
                if (listener != null) listener.onError(errorMsg);
            }

            @Override
            public void onResults(Bundle results) {
                isListening = false;
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String recognizedText = matches.get(0);
                    if (listener != null) listener.onResult(recognizedText);
                } else {
                    if (listener != null) listener.onError("No speech recognized");
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });

        speechRecognizer.startListening(intent);
    }

    public void stopListening() {
        if (speechRecognizer != null) {
            try {
                speechRecognizer.stopListening();
                speechRecognizer.destroy();
            } catch (Exception ignored) {}
            speechRecognizer = null;
        }
        isListening = false;
    }

    private String getErrorMessage(int errorCode) {
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                return "Audio recording error";
            case SpeechRecognizer.ERROR_CLIENT:
                return "Client side error";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                return "Insufficient permissions";
            case SpeechRecognizer.ERROR_NETWORK:
                return "Network error";
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                return "Network timeout";
            case SpeechRecognizer.ERROR_NO_MATCH:
                return "No speech recognized. Try speaking again.";
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                return "Speech service busy";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                return "No speech input detected";
            default:
                return "Speech recognition error";
        }
    }
}