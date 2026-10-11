package com.example.chatbot1.multimodal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.InputStream;

public class ImageProcessor {

    public interface OnTextExtractedListener {
        void onSuccess(String extractedText);
        void onFailure(Exception e);
    }

    private final TextRecognizer textRecognizer;

    public ImageProcessor() {
        this.textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    public void processImage(Context context, Uri imageUri, OnTextExtractedListener listener) {
        if (context == null || imageUri == null) {
            if (listener != null) {
                listener.onFailure(new IllegalArgumentException("Invalid image or context"));
            }
            return;
        }

        try {
            Bitmap bitmap = null;
            try (InputStream is = context.getContentResolver().openInputStream(imageUri)) {
                if (is != null) {
                    bitmap = BitmapFactory.decodeStream(is);
                }
            } catch (Exception ignored) {}

            InputImage inputImage;
            if (bitmap != null) {
                inputImage = InputImage.fromBitmap(bitmap, 0);
            } else {
                inputImage = InputImage.fromFilePath(context, imageUri);
            }

            textRecognizer.process(inputImage)
                    .addOnSuccessListener(visionText -> {
                        String text = visionText.getText();
                        if (listener != null) {
                            listener.onSuccess(text != null ? text.trim() : "");
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (listener != null) {
                            listener.onFailure(e);
                        }
                    });
        } catch (Exception e) {
            if (listener != null) {
                listener.onFailure(e);
            }
        }
    }
}