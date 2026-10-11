package com.example.chatbot1.nlp;

import com.example.chatbot1.model.ChatMessage;
import com.example.chatbot1.model.Topic;
import com.example.chatbot1.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ResponseManager {

    /**
     * Generates a fully formatted ChatMessage response for the analyzed query.
     */
    public ChatMessage generateResponse(QueryAnalyzer.AnalysisResult analysis, ConversationManager conversationManager) {
        String currentTime = Utils.getCurrentTimestamp();

        // 1. Handle Out-of-Domain
        if (analysis.isOutOfDomain) {
            String text = "I am a **Machine Learning Educational Assistant**.\n\n"
                    + "I can help you with Machine Learning concepts, algorithms, models, evaluation, data preprocessing, and related topics.\n\n"
                    + "Please ask me a Machine Learning question!";
            ChatMessage botMsg = new ChatMessage(ChatMessage.TYPE_BOT, text, currentTime, "OUT_OF_DOMAIN", "Domain Restriction");
            List<String> suggestions = new ArrayList<>();
            suggestions.add("What is Machine Learning?");
            suggestions.add("What are the types of Machine Learning?");
            suggestions.add("Explain Supervised Learning");
            suggestions.add("Explain SVM");
            botMsg.setSuggestedQuestions(suggestions);
            return botMsg;
        }

        // 2. Handle Ambiguous / Clarification
        if (analysis.isAmbiguous) {
            StringBuilder sb = new StringBuilder("Machine Learning uses terms in multiple contexts. Could you clarify what you mean?\n\n");
            for (int idx = 0; idx < analysis.clarificationOptions.size(); idx++) {
                sb.append(idx + 1).append(". ").append(analysis.clarificationOptions.get(idx)).append("\n");
            }
            ChatMessage botMsg = new ChatMessage(ChatMessage.TYPE_BOT, sb.toString(), currentTime, "AMBIGUOUS", "Clarification Needed");
            botMsg.setSuggestedQuestions(analysis.clarificationOptions);
            return botMsg;
        }

        // 3. Handle Pure Conversational / Social Messages (ONLY when strictly social)
        if (analysis.intent == IntentClassifier.Intent.GREETING) {
            String text = "Hello! 👋 How can I help you with Machine Learning today?";
            ChatMessage botMsg = new ChatMessage(ChatMessage.TYPE_BOT, text, currentTime, "GREETING", "Greeting");
            List<String> suggestions = new ArrayList<>();
            suggestions.add("What is Machine Learning?");
            suggestions.add("What are the types of Machine Learning?");
            suggestions.add("Explain Supervised Learning");
            suggestions.add("Explain SVM");
            botMsg.setSuggestedQuestions(suggestions);
            return botMsg;
        }

        if (analysis.intent == IntentClassifier.Intent.THANK_YOU) {
            String text = "You're welcome! 😊 Feel free to ask if you have more questions about Machine Learning.";
            ChatMessage botMsg = new ChatMessage(ChatMessage.TYPE_BOT, text, currentTime, "CONVERSATIONAL", "Polite Response");
            List<String> suggestions = new ArrayList<>();
            suggestions.add("What is Machine Learning?");
            suggestions.add("Explain SVM");
            botMsg.setSuggestedQuestions(suggestions);
            return botMsg;
        }

        if (analysis.intent == IntentClassifier.Intent.ACKNOWLEDGEMENT) {
            String text = "Great! Let me know if you need help with any Machine Learning concept.";
            ChatMessage botMsg = new ChatMessage(ChatMessage.TYPE_BOT, text, currentTime, "CONVERSATIONAL", "Acknowledgement");
            List<String> suggestions = new ArrayList<>();
            suggestions.add("Explain Decision Tree");
            suggestions.add("Explain Random Forest");
            botMsg.setSuggestedQuestions(suggestions);
            return botMsg;
        }

        if (analysis.intent == IntentClassifier.Intent.FAREWELL) {
            String text = "Goodbye! 👋 Happy learning, and feel free to return whenever you have ML questions.";
            return new ChatMessage(ChatMessage.TYPE_BOT, text, currentTime, "CONVERSATIONAL", "Farewell");
        }

        // 4. Handle Help
        if (analysis.intent == IntentClassifier.Intent.HELP) {
            String text = "### How I Can Help You Learn ML\n\n"
                    + "• **Ask Fundamentals**: 'What is ML?', 'What are the types of ML?', 'AI vs ML'\n"
                    + "• **Learn Specific Algorithms**: 'Explain SVM', 'Explain KNN', 'Explain Decision Tree'\n"
                    + "• **Ask Specific Aspects**: 'How does SVM work?', 'What are the advantages of SVM?'\n"
                    + "• **Request Deep Answers**: 'Explain SVM in detail' or 'Explain Random Forest in detail'\n"
                    + "• **Compare Topics**: 'SVM vs KNN', 'Decision Tree vs Random Forest'";
            ChatMessage botMsg = new ChatMessage(ChatMessage.TYPE_BOT, text, currentTime, "HELP", "Capabilities");
            List<String> suggestions = new ArrayList<>();
            suggestions.add("Explain SVM");
            suggestions.add("How does SVM work?");
            suggestions.add("SVM vs KNN");
            botMsg.setSuggestedQuestions(suggestions);
            return botMsg;
        }

        // 5. Handle ML Topic Match
        Topic topic = analysis.matchedTopic;

        if (topic == null) {
            String text = "I'm sorry, I couldn't find a direct match for that Machine Learning topic in my local knowledge base.\n\n"
                    + "Try asking about:\n"
                    + "• Machine Learning Types (Supervised, Unsupervised, Reinforcement)\n"
                    + "• Algorithms (SVM, KNN, Decision Tree, Random Forest, Linear/Logistic Regression)\n"
                    + "• Evaluation (Accuracy, Precision, Recall, F1 Score, Confusion Matrix)\n"
                    + "• Data Preprocessing (Normalization, Standardization, Encoding)";
            ChatMessage botMsg = new ChatMessage(ChatMessage.TYPE_BOT, text, currentTime, "UNKNOWN", "Not Found");
            List<String> suggestions = new ArrayList<>();
            suggestions.add("What is Machine Learning?");
            suggestions.add("What are the types of Machine Learning?");
            suggestions.add("Explain SVM");
            botMsg.setSuggestedQuestions(suggestions);
            return botMsg;
        }

        // Lock Context strictly to Matched Topic
        conversationManager.updateContext(topic.getId(), topic.getTitle(), analysis.intent, analysis.answerLevel);

        // Build Focused Response according to Topic + Intent (NO conversational greetings appended/prepended!)
        String responseText;
        String levelBadge;

        IntentClassifier.Intent intent = analysis.intent;

        if ("COMPARISON".equals(analysis.answerLevel) || intent == IntentClassifier.Intent.COMPARISON) {
            Topic otherTopic = analysis.comparisonTargetTopic;
            if (otherTopic != null && topic.getComparisons().containsKey(otherTopic.getId())) {
                responseText = topic.getComparisons().get(otherTopic.getId());
            } else if (otherTopic != null) {
                responseText = "### Comparison: " + topic.getTitle() + " vs " + otherTopic.getTitle() + "\n\n"
                        + "1. **" + topic.getTitle() + "**: " + topic.getShortAnswer() + "\n\n"
                        + "2. **" + otherTopic.getTitle() + "**: " + otherTopic.getShortAnswer();
            } else {
                if (!topic.getComparisons().isEmpty()) {
                    responseText = topic.getComparisons().values().iterator().next();
                } else {
                    responseText = "### " + topic.getTitle() + "\n\n" + topic.getNormalAnswer();
                }
            }
            levelBadge = "COMPARISON";
        } else if (intent == IntentClassifier.Intent.ADVANTAGES) {
            StringBuilder sb = new StringBuilder("### Advantages of " + topic.getTitle() + "\n\n");
            if (topic.getAdvantages() != null && !topic.getAdvantages().isEmpty()) {
                for (String adv : topic.getAdvantages()) {
                    sb.append("• ").append(adv).append("\n");
                }
            } else {
                sb.append(topic.getNormalAnswer());
            }
            responseText = sb.toString();
            levelBadge = "NORMAL";
        } else if (intent == IntentClassifier.Intent.LIMITATIONS) {
            StringBuilder sb = new StringBuilder("### Limitations of " + topic.getTitle() + "\n\n");
            if (topic.getLimitations() != null && !topic.getLimitations().isEmpty()) {
                for (String lim : topic.getLimitations()) {
                    sb.append("• ").append(lim).append("\n");
                }
            } else {
                sb.append(topic.getNormalAnswer());
            }
            responseText = sb.toString();
            levelBadge = "NORMAL";
        } else if (intent == IntentClassifier.Intent.WORKING) {
            StringBuilder sb = new StringBuilder("### How " + topic.getTitle() + " Works\n\n");
            if (topic.getWorking() != null && !topic.getWorking().isEmpty()) {
                sb.append(topic.getWorking());
            } else {
                sb.append(topic.getNormalAnswer());
            }
            responseText = sb.toString();
            levelBadge = "NORMAL";
        } else if (intent == IntentClassifier.Intent.EXAMPLES) {
            StringBuilder sb = new StringBuilder("### Practical Examples of " + topic.getTitle() + "\n\n");
            if (topic.getExamples() != null && !topic.getExamples().isEmpty()) {
                for (String ex : topic.getExamples()) {
                    sb.append("• ").append(ex).append("\n");
                }
            } else {
                sb.append(topic.getNormalAnswer());
            }
            responseText = sb.toString();
            levelBadge = "NORMAL";
        } else if (intent == IntentClassifier.Intent.APPLICATIONS) {
            StringBuilder sb = new StringBuilder("### Applications of " + topic.getTitle() + "\n\n");
            if (topic.getApplications() != null && !topic.getApplications().isEmpty()) {
                for (String app : topic.getApplications()) {
                    sb.append("• ").append(app).append("\n");
                }
            } else {
                sb.append(topic.getNormalAnswer());
            }
            responseText = sb.toString();
            levelBadge = "NORMAL";
        } else if ("DEEP".equals(analysis.answerLevel) || intent == IntentClassifier.Intent.DEEP_EXPLANATION) {
            responseText = topic.getDeepAnswer();
            levelBadge = "DEEP";
        } else if ("QUICK".equals(analysis.answerLevel) || intent == IntentClassifier.Intent.DEFINITION) {
            responseText = "### " + topic.getTitle() + "\n\n" + topic.getShortAnswer();
            levelBadge = "QUICK";
        } else {
            // Default NORMAL Explanation for topic
            responseText = "### " + topic.getTitle() + "\n\n" + topic.getNormalAnswer();
            levelBadge = "NORMAL";
        }

        ChatMessage botMsg = new ChatMessage(ChatMessage.TYPE_BOT, responseText, currentTime, levelBadge, topic.getTitle());

        // Generate dynamic follow-up suggestions for topic lock
        List<String> followUps = generateFollowUpSuggestions(topic, analysis.answerLevel, intent);
        botMsg.setSuggestedQuestions(followUps);

        return botMsg;
    }

    private List<String> generateFollowUpSuggestions(Topic topic, String currentLevel, IntentClassifier.Intent intent) {
        List<String> suggestions = new ArrayList<>();

        if (intent != IntentClassifier.Intent.WORKING) {
            suggestions.add("How does " + topic.getTitle() + " work?");
        }

        if (intent != IntentClassifier.Intent.ADVANTAGES && topic.getAdvantages() != null && !topic.getAdvantages().isEmpty()) {
            suggestions.add("What are its advantages?");
        }

        if (intent != IntentClassifier.Intent.LIMITATIONS && topic.getLimitations() != null && !topic.getLimitations().isEmpty()) {
            suggestions.add("What are its limitations?");
        }

        if (!"DEEP".equals(currentLevel) && intent != IntentClassifier.Intent.DEEP_EXPLANATION) {
            suggestions.add("Explain " + topic.getTitle() + " in detail");
        }

        if ("svm".equals(topic.getId())) {
            suggestions.add("SVM vs KNN");
        } else if ("knn".equals(topic.getId())) {
            suggestions.add("KNN vs Decision Tree");
        } else if ("decision_tree".equals(topic.getId())) {
            suggestions.add("Decision Tree vs Random Forest");
        } else if ("what_is_ml".equals(topic.getId())) {
            suggestions.add("What are the types of Machine Learning?");
        }

        return suggestions;
    }
}