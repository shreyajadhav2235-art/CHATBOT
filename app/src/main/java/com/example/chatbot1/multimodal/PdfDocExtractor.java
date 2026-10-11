package com.example.chatbot1.multimodal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PdfDocExtractor {

    public interface OnExtractionListener {
        void onSuccess(String extractedText);
        void onFailure(Exception e);
    }

    private final ExecutorService executor;
    private final Handler mainHandler;
    private final TextRecognizer textRecognizer;

    public PdfDocExtractor() {
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    public void extractText(Context context, Uri uri, String mimeType, String fileName, OnExtractionListener listener) {
        if (context == null || uri == null) {
            if (listener != null) listener.onFailure(new IllegalArgumentException("Invalid file or context"));
            return;
        }

        executor.execute(() -> {
            try {
                String resultText;
                String lowerName = fileName != null ? fileName.toLowerCase() : "";

                if (lowerName.endsWith(".pdf") || "application/pdf".equalsIgnoreCase(mimeType)) {
                    resultText = extractFromPdf(context, uri);
                } else if (lowerName.endsWith(".txt") || "text/plain".equalsIgnoreCase(mimeType)) {
                    resultText = extractFromTxt(context, uri);
                } else {
                    resultText = extractFromStream(context, uri);
                }

                String finalResult = resultText.trim();
                mainHandler.post(() -> {
                    if (listener != null) listener.onSuccess(finalResult);
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (listener != null) listener.onFailure(e);
                });
            }
        });
    }

    private String extractFromTxt(Context context, Uri uri) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = context.getContentResolver().openInputStream(uri)) {
            if (is != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                }
            }
        }
        return sb.toString();
    }

    private String extractFromStream(Context context, Uri uri) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = context.getContentResolver().openInputStream(uri)) {
            if (is != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    char[] buffer = new char[1024];
                    int numRead;
                    while ((numRead = reader.read(buffer)) != -1) {
                        String chunk = new String(buffer, 0, numRead);
                        chunk = chunk.replaceAll("[^\\x20-\\x7E\\s]", " ");
                        sb.append(chunk);
                    }
                }
            }
        }
        return sb.toString();
    }

    private String extractFromPdf(Context context, Uri uri) throws Exception {
        File tempFile = new File(context.getCacheDir(), "temp_pdf_render.pdf");
        try (InputStream is = context.getContentResolver().openInputStream(uri)) {
            if (is == null) return "";
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
            }
        }

        StringBuilder pdfText = new StringBuilder();

        try (ParcelFileDescriptor pfd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY);
             PdfRenderer renderer = new PdfRenderer(pfd)) {

            int pageCount = Math.min(renderer.getPageCount(), 10);
            for (int i = 0; i < pageCount; i++) {
                PdfRenderer.Page page = renderer.openPage(i);
                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                page.close();

                InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
                Task<Text> task = textRecognizer.process(inputImage);
                Text textResult = Tasks.await(task);

                if (textResult != null && textResult.getText() != null) {
                    pdfText.append("--- Page ").append(i + 1).append(" ---\n");
                    pdfText.append(textResult.getText()).append("\n\n");
                }
                bitmap.recycle();
            }
        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }

        return pdfText.toString();
    }
}