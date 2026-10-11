package com.example.chatbot1.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chatbot1.R;
import com.google.android.material.card.MaterialCardView;

public class CodeWorkspaceDialog {

    public interface OnCodeSendListener {
        void onCodeSelected(String codePrompt);
    }

    public static void show(Context context, OnCodeSendListener listener) {
        if (context == null) return;

        Dialog dialog = new Dialog(context, R.style.FullScreenDialogTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_full_screen_feature, null);

        TextView tvTitle = view.findViewById(R.id.tvTitleFullScreen);
        TextView tvSubtitle = view.findViewById(R.id.tvSubtitleFullScreen);
        ImageView btnBack = view.findViewById(R.id.btnBackFullScreen);
        LinearLayout container = view.findViewById(R.id.containerFullScreenContent);

        if (tvTitle != null) tvTitle.setText("Python ML Coding Workspace");
        if (tvSubtitle != null) tvSubtitle.setText("Run and explore Python ML code templates");

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> dialog.dismiss());
        }

        String[] titles = new String[]{
                "🐍 Scikit-Learn: Random Forest Classifier Template",
                "📊 Pandas: Data Preprocessing & Missing Values",
                "📉 Linear & Logistic Regression in Python",
                "🧠 PyTorch: Basic Neural Network Training Loop",
                "⚙️ Hyperparameter Tuning using GridSearchCV"
        };

        String[] codeSnippets = new String[]{
                "Explain and write Scikit-Learn code for Random Forest Classifier:\n\n```python\nfrom sklearn.ensemble import RandomForestClassifier\nfrom sklearn.model_selection import train_test_split\nfrom sklearn.metrics import accuracy_score\n\nX_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)\nclf = RandomForestClassifier(n_estimators=100, max_depth=10)\nclf.fit(X_train, y_train)\npreds = clf.predict(X_test)\nprint('Accuracy:', accuracy_score(y_test, preds))\n```",
                "Write Python Pandas code for Data Preprocessing and handling missing values:\n\n```python\nimport pandas as pd\nimport numpy as np\n\ndf = pd.read_csv('dataset.csv')\ndf.fillna(df.median(), inplace=True)\ndf = pd.get_dummies(df, drop_first=True)\nprint(df.head())\n```",
                "Write Python code comparing Linear Regression and Logistic Regression:\n\n```python\nfrom sklearn.linear_model import LinearRegression, LogisticRegression\n\nreg = LinearRegression().fit(X_train, y_train_reg)\nclf = LogisticRegression().fit(X_train, y_train_cls)\n```",
                "Write PyTorch Neural Network training loop:\n\n```python\nimport torch\nimport torch.nn as nn\n\nclass Net(nn.Module):\n    def __init__(self):\n        super().__init__()\n        self.fc = nn.Linear(10, 2)\n    def forward(self, x):\n        return self.fc(x)\n```",
                "Write Scikit-Learn GridSearchCV hyperparameter tuning code:\n\n```python\nfrom sklearn.model_selection import GridSearchCV\nparam_grid = {'n_estimators': [50, 100], 'max_depth': [5, 10]}\ngrid = GridSearchCV(estimator, param_grid, cv=5)\ngrid.fit(X_train, y_train)\n```"
        };

        for (int i = 0; i < titles.length; i++) {
            final String codeSnippet = codeSnippets[i];

            MaterialCardView card = new MaterialCardView(context);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(0, 0, 0, 16);
            card.setLayoutParams(cardParams);
            card.setCardBackgroundColor(context.getColor(R.color.bot_bubble_bg));
            card.setStrokeColor(context.getColor(R.color.border_color));
            card.setStrokeWidth(2);
            card.setRadius(24);

            TextView tvItem = new TextView(context);
            tvItem.setText(titles[i]);
            tvItem.setTextColor(context.getColor(R.color.text_primary));
            tvItem.setTextSize(15);
            tvItem.setTypeface(null, Typeface.BOLD);
            tvItem.setPadding(32, 28, 32, 28);

            card.addView(tvItem);
            card.setOnClickListener(v -> {
                dialog.dismiss();
                if (listener != null) {
                    listener.onCodeSelected(codeSnippet);
                }
            });

            container.addView(card);
        }

        dialog.setContentView(view);
        dialog.show();
    }
}