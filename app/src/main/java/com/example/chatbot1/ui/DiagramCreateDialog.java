package com.example.chatbot1.ui;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;

public class DiagramCreateDialog {

    public interface OnDiagramSelectedListener {
        void onDiagramPromptSelected(String promptText);
    }

    public static void show(Context context, OnDiagramSelectedListener listener) {
        if (context == null) return;

        CharSequence[] options = new CharSequence[]{
                "🧠 Artificial Neural Network (ANN) Architecture",
                "🌲 Decision Tree Workflow Diagram",
                "📊 Confusion Matrix & Classification Metrics Chart",
                "🔄 End-to-End Machine Learning Pipeline",
                "📈 Linear vs Non-Linear Decision Boundary"
        };

        new AlertDialog.Builder(context)
                .setTitle("🎨 Create ML Diagram / Flowchart")
                .setItems(options, (dialog, which) -> {
                    String selectedPrompt = "";
                    switch (which) {
                        case 0:
                            selectedPrompt = "Generate and explain Artificial Neural Network (ANN) Architecture diagram with Input, Hidden, and Output layers.";
                            break;
                        case 1:
                            selectedPrompt = "Generate and explain Decision Tree split flowchart diagram with Root Node, Internal Splits, and Leaf Nodes.";
                            break;
                        case 2:
                            selectedPrompt = "Create a detailed Confusion Matrix chart explaining True Positives, True Negatives, False Positives, and False Negatives.";
                            break;
                        case 3:
                            selectedPrompt = "Explain the End-to-End Machine Learning Pipeline Workflow diagram (Data Extraction ➔ Preprocessing ➔ Feature Engineering ➔ Training ➔ Evaluation ➔ Deployment).";
                            break;
                        case 4:
                            selectedPrompt = "Explain Linear vs Non-Linear Decision Boundary and Kernel Trick concept with diagrammatic examples.";
                            break;
                    }
                    if (listener != null) {
                        listener.onDiagramPromptSelected(selectedPrompt);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}