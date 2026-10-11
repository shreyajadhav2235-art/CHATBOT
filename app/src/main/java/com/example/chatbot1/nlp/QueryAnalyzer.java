package com.example.chatbot1.nlp;

import com.example.chatbot1.kb.KnowledgeBase;
import com.example.chatbot1.model.Topic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QueryAnalyzer {

    private final NLPProcessor nlpProcessor;
    private final IntentClassifier intentClassifier;
    private final KnowledgeBase knowledgeBase;

    // Out of domain triggers
    private static final Set<String> OUT_OF_DOMAIN_KEYWORDS = new HashSet<>(Arrays.asList(
            "cricket", "football", "baseball", "politics", "president", "prime minister", "weather", "rain",
            "temperature", "movie", "actor", "actress", "song", "music", "recipe", "food", "cooking",
            "capital", "country", "bitcoin", "crypto", "joke", "story", "game"
    ));

    // ML Connection keywords that save domain cross-over questions
    private static final Set<String> ML_CROSSOVER_TERMS = new HashSet<>(Arrays.asList(
            "ml", "machine learning", "ai", "artificial intelligence", "algorithm", "model", "predict",
            "prediction", "analytics", "data", "forecast", "classification", "regression", "tracking"
    ));

    // High Priority Explicit Phrase Mapping
    private static final Map<String, String> HIGH_PRIORITY_PHRASES = new HashMap<>();

    static {
        // Types of ML vs What is ML
        HIGH_PRIORITY_PHRASES.put("types of machine learning", "ml_types");
        HIGH_PRIORITY_PHRASES.put("types of ml", "ml_types");
        HIGH_PRIORITY_PHRASES.put("categories of machine learning", "ml_types");
        HIGH_PRIORITY_PHRASES.put("categories of ml", "ml_types");
        HIGH_PRIORITY_PHRASES.put("different types of machine learning", "ml_types");
        HIGH_PRIORITY_PHRASES.put("different types of ml", "ml_types");
        HIGH_PRIORITY_PHRASES.put("how many types of machine learning", "ml_types");
        HIGH_PRIORITY_PHRASES.put("how many types of ml", "ml_types");
        HIGH_PRIORITY_PHRASES.put("explain different types of ml", "ml_types");
        HIGH_PRIORITY_PHRASES.put("explain types of ml", "ml_types");

        // Specific Algorithms
        HIGH_PRIORITY_PHRASES.put("support vector machine", "svm");
        HIGH_PRIORITY_PHRASES.put("support vector machines", "svm");
        HIGH_PRIORITY_PHRASES.put("svm", "svm");

        HIGH_PRIORITY_PHRASES.put("k nearest neighbors", "knn");
        HIGH_PRIORITY_PHRASES.put("k nearest neighbor", "knn");
        HIGH_PRIORITY_PHRASES.put("k-nearest neighbors", "knn");
        HIGH_PRIORITY_PHRASES.put("nearest neighbors", "knn");
        HIGH_PRIORITY_PHRASES.put("knn", "knn");

        HIGH_PRIORITY_PHRASES.put("random forest", "random_forest");
        HIGH_PRIORITY_PHRASES.put("random forests", "random_forest");

        HIGH_PRIORITY_PHRASES.put("decision tree", "decision_tree");
        HIGH_PRIORITY_PHRASES.put("decision trees", "decision_tree");

        HIGH_PRIORITY_PHRASES.put("linear regression", "linear_regression");
        HIGH_PRIORITY_PHRASES.put("logistic regression", "logistic_regression");
        HIGH_PRIORITY_PHRASES.put("naive bayes", "naive_bayes");

        HIGH_PRIORITY_PHRASES.put("gradient boosting", "gradient_boosting");
        HIGH_PRIORITY_PHRASES.put("xgboost", "gradient_boosting");
        HIGH_PRIORITY_PHRASES.put("lightgbm", "gradient_boosting");
        HIGH_PRIORITY_PHRASES.put("catboost", "gradient_boosting");

        HIGH_PRIORITY_PHRASES.put("k means", "kmeans");
        HIGH_PRIORITY_PHRASES.put("kmeans", "kmeans");
        HIGH_PRIORITY_PHRASES.put("k-means", "kmeans");

        HIGH_PRIORITY_PHRASES.put("principal component analysis", "pca");
        HIGH_PRIORITY_PHRASES.put("pca", "pca");

        HIGH_PRIORITY_PHRASES.put("neural network", "neural_networks");
        HIGH_PRIORITY_PHRASES.put("neural networks", "neural_networks");
        HIGH_PRIORITY_PHRASES.put("deep learning", "ml_vs_dl");

        // Fundamentals & Preprocessing & Problems
        HIGH_PRIORITY_PHRASES.put("supervised learning", "supervised_learning");
        HIGH_PRIORITY_PHRASES.put("unsupervised learning", "unsupervised_learning");
        HIGH_PRIORITY_PHRASES.put("semi supervised learning", "semi_supervised_learning");
        HIGH_PRIORITY_PHRASES.put("reinforcement learning", "reinforcement_learning");

        HIGH_PRIORITY_PHRASES.put("classification", "classification");
        HIGH_PRIORITY_PHRASES.put("regression", "regression");

        HIGH_PRIORITY_PHRASES.put("overfitting", "overfitting");
        HIGH_PRIORITY_PHRASES.put("underfitting", "underfitting");
        HIGH_PRIORITY_PHRASES.put("bias variance tradeoff", "bias_variance_tradeoff");
        HIGH_PRIORITY_PHRASES.put("bias variance", "bias_variance_tradeoff");
        HIGH_PRIORITY_PHRASES.put("bias vs variance", "bias_variance_tradeoff");

        HIGH_PRIORITY_PHRASES.put("cross validation", "cross_validation");
        HIGH_PRIORITY_PHRASES.put("feature engineering", "feature_engineering");
        HIGH_PRIORITY_PHRASES.put("feature scaling", "feature_scaling");
        HIGH_PRIORITY_PHRASES.put("normalization", "normalization");
        HIGH_PRIORITY_PHRASES.put("standardization", "standardization");
        HIGH_PRIORITY_PHRASES.put("confusion matrix", "confusion_matrix");
        HIGH_PRIORITY_PHRASES.put("hyperparameter tuning", "hyperparameter_tuning");
        HIGH_PRIORITY_PHRASES.put("regularization", "regularization");
        HIGH_PRIORITY_PHRASES.put("gradient descent", "gradient_descent");
        HIGH_PRIORITY_PHRASES.put("loss function", "loss_functions");

        HIGH_PRIORITY_PHRASES.put("accuracy", "accuracy");
        HIGH_PRIORITY_PHRASES.put("precision", "precision");
        HIGH_PRIORITY_PHRASES.put("recall", "recall");
        HIGH_PRIORITY_PHRASES.put("f1 score", "f1_score");

        HIGH_PRIORITY_PHRASES.put("what is machine learning", "what_is_ml");
        HIGH_PRIORITY_PHRASES.put("define machine learning", "what_is_ml");
        HIGH_PRIORITY_PHRASES.put("what is ml", "what_is_ml");
        HIGH_PRIORITY_PHRASES.put("concept of ml", "what_is_ml");
    }

    public QueryAnalyzer(KnowledgeBase knowledgeBase) {
        this.nlpProcessor = new NLPProcessor();
        this.intentClassifier = new IntentClassifier();
        this.knowledgeBase = knowledgeBase;
    }

    public static class AnalysisResult {
        public String query;
        public String normalizedText;
        public IntentClassifier.Intent intent;
        public String answerLevel; // "QUICK", "NORMAL", "DEEP", "COMPARISON", "OUT_OF_DOMAIN", "AMBIGUOUS"
        public Topic matchedTopic;
        public Topic comparisonTargetTopic;
        public boolean isOutOfDomain;
        public boolean isAmbiguous;
        public List<String> clarificationOptions;

        public AnalysisResult(String query) {
            this.query = query;
            this.clarificationOptions = new ArrayList<>();
        }
    }

    public AnalysisResult analyze(String rawQuery, ConversationManager conversationManager) {
        AnalysisResult result = new AnalysisResult(rawQuery);

        if (rawQuery == null || rawQuery.trim().isEmpty()) {
            result.isAmbiguous = true;
            result.answerLevel = "AMBIGUOUS";
            return result;
        }

        // Step 1: Text Normalization
        String normalized = nlpProcessor.normalize(rawQuery);
        result.normalizedText = normalized;

        // Step 2: Tokenization & Filtering
        List<String> tokens = nlpProcessor.tokenize(normalized);
        List<String> filteredTokens = nlpProcessor.filterStopWords(tokens);
        List<String> nGrams = nlpProcessor.extractNGrams(filteredTokens);

        // Step 3: Initial Intent Classification
        IntentClassifier.Intent intent = intentClassifier.classifyIntent(normalized, nGrams);

        // Step 4: Check for Out-of-Domain questions
        if (checkOutOfDomain(normalized, tokens)) {
            result.isOutOfDomain = true;
            result.answerLevel = "OUT_OF_DOMAIN";
            result.intent = IntentClassifier.Intent.EXPLANATION;
            return result;
        }

        // Step 5: Check for Ambiguous / Single Word vague query
        if (filteredTokens.size() == 1) {
            String singleWord = filteredTokens.get(0);
            if ("model".equals(singleWord)) {
                result.isAmbiguous = true;
                result.answerLevel = "AMBIGUOUS";
                result.clarificationOptions.add("What is a Machine Learning model?");
                result.clarificationOptions.add("How is a model trained?");
                result.clarificationOptions.add("How is a model evaluated?");
                return result;
            } else if ("tree".equals(singleWord) || "trees".equals(singleWord)) {
                result.isAmbiguous = true;
                result.answerLevel = "AMBIGUOUS";
                result.clarificationOptions.add("Explain Decision Tree.");
                result.clarificationOptions.add("Explain Random Forest.");
                result.clarificationOptions.add("Decision Tree vs Random Forest.");
                return result;
            } else if ("learning".equals(singleWord)) {
                result.isAmbiguous = true;
                result.answerLevel = "AMBIGUOUS";
                result.clarificationOptions.add("What is Machine Learning?");
                result.clarificationOptions.add("Supervised vs Unsupervised Learning.");
                result.clarificationOptions.add("What is Reinforcement Learning?");
                return result;
            }
        }

        // Step 6: Topic Identification following strict priority
        Topic primaryTopic = null;
        Topic secondaryTopic = null;

        // Check if query is a pure social message ("Hello", "Hi", "Thank you", "Okay", "Bye")
        if (isSocialIntent(intent)) {
            List<String> detectedTopicIds = detectTopicsByPriorityPhrases(normalized);
            if (!detectedTopicIds.isEmpty() || normalized.startsWith("what is") || normalized.contains("explain") || normalized.contains("how")) {
                // User asked an explicit ML question in the same message (e.g. "Hello, explain SVM")
                if (!detectedTopicIds.isEmpty()) {
                    primaryTopic = knowledgeBase.getTopic(detectedTopicIds.get(0));
                    if (detectedTopicIds.size() > 1) {
                        secondaryTopic = knowledgeBase.getTopic(detectedTopicIds.get(1));
                    }
                }
                if (normalized.startsWith("what is") || normalized.startsWith("define")) {
                    intent = IntentClassifier.Intent.DEFINITION;
                } else if (normalized.contains("detail") || normalized.contains("deeply")) {
                    intent = IntentClassifier.Intent.DEEP_EXPLANATION;
                } else {
                    intent = IntentClassifier.Intent.EXPLANATION;
                }
            } else {
                // Pure social conversational message ("Hello", "Hi", "Thank you", "Okay", "Bye") -> Keep topic null!
                result.intent = intent;
                result.matchedTopic = null;
                result.comparisonTargetTopic = null;
                result.answerLevel = "NORMAL";
                return result;
            }
        }

        // Priority 1: Specific Multi-Word Phrase Detection
        if (primaryTopic == null) {
            List<String> detectedTopicIds = detectTopicsByPriorityPhrases(normalized);
            if (!detectedTopicIds.isEmpty()) {
                primaryTopic = knowledgeBase.getTopic(detectedTopicIds.get(0));
                if (detectedTopicIds.size() > 1) {
                    secondaryTopic = knowledgeBase.getTopic(detectedTopicIds.get(1));
                }
            }
        }

        // Priority 2: Follow-up Resolution via Conversation Context (ONLY for non-social follow-up queries)
        if (primaryTopic == null && (intent == IntentClassifier.Intent.FOLLOW_UP || isFollowUpIntent(intent)) && conversationManager.hasActiveTopic()) {
            primaryTopic = knowledgeBase.getTopic(conversationManager.getLastTopicId());
        }

        // Priority 3: NLP Similarity Matching against Knowledge Base
        if (primaryTopic == null) {
            Topic[] matchedTopics = findBestMatchingTopics(normalized, nGrams, filteredTokens);
            primaryTopic = matchedTopics[0];
            secondaryTopic = matchedTopics[1];
        }

        // Fallback for Follow-up if still null
        if (primaryTopic == null && intent == IntentClassifier.Intent.FOLLOW_UP && conversationManager.hasActiveTopic()) {
            primaryTopic = knowledgeBase.getTopic(conversationManager.getLastTopicId());
        }

        result.intent = intent;
        result.matchedTopic = primaryTopic;
        result.comparisonTargetTopic = secondaryTopic;

        // Step 7: Depth Detection
        String depthLevel = determineDepthLevel(normalized, intent);

        // Step 8: Adjust answer level for comparison
        if (intent == IntentClassifier.Intent.COMPARISON || (primaryTopic != null && secondaryTopic != null)) {
            result.answerLevel = "COMPARISON";
        } else {
            result.answerLevel = depthLevel;
        }

        return result;
    }

    private boolean isSocialIntent(IntentClassifier.Intent intent) {
        return intent == IntentClassifier.Intent.GREETING
                || intent == IntentClassifier.Intent.THANK_YOU
                || intent == IntentClassifier.Intent.ACKNOWLEDGEMENT
                || intent == IntentClassifier.Intent.FAREWELL;
    }

    private boolean isFollowUpIntent(IntentClassifier.Intent intent) {
        return intent == IntentClassifier.Intent.WORKING || intent == IntentClassifier.Intent.ADVANTAGES
                || intent == IntentClassifier.Intent.LIMITATIONS || intent == IntentClassifier.Intent.EXAMPLES
                || intent == IntentClassifier.Intent.APPLICATIONS;
    }

    private List<String> detectTopicsByPriorityPhrases(String normalizedText) {
        List<String> matchedIds = new ArrayList<>();

        for (Map.Entry<String, String> entry : HIGH_PRIORITY_PHRASES.entrySet()) {
            String phrase = entry.getKey();
            String topicId = entry.getValue();

            if (normalizedText.contains(phrase)) {
                if (!matchedIds.contains(topicId)) {
                    matchedIds.add(topicId);
                }
            }
        }

        return matchedIds;
    }

    private boolean checkOutOfDomain(String normalizedText, List<String> tokens) {
        boolean containsOodKeyword = false;

        for (String token : tokens) {
            if (OUT_OF_DOMAIN_KEYWORDS.contains(token)) {
                containsOodKeyword = true;
                break;
            }
        }

        if (!containsOodKeyword) {
            return false;
        }

        // Exception check: Is there an ML connection?
        for (String term : ML_CROSSOVER_TERMS) {
            if (normalizedText.contains(term)) {
                return false; // Valid ML cross-over question!
            }
        }

        return true;
    }

    private String determineDepthLevel(String normalizedText, IntentClassifier.Intent intent) {
        if (intent == IntentClassifier.Intent.DEEP_EXPLANATION) {
            return "DEEP";
        }

        if (normalizedText.contains("detail") || normalizedText.contains("deeply")
                || normalizedText.contains("deep explanation") || normalizedText.contains("complete information")
                || normalizedText.contains("step by step") || normalizedText.contains("everything about")
                || normalizedText.contains("advantages and disadvantages") || normalizedText.contains("thoroughly")) {
            return "DEEP";
        }

        if (normalizedText.startsWith("what is") || normalizedText.startsWith("define")
                || normalizedText.startsWith("meaning of")) {
            if (normalizedText.length() < 30) {
                return "QUICK";
            }
        }

        return "NORMAL";
    }

    private Topic[] findBestMatchingTopics(String normalizedText, List<String> nGrams, List<String> filteredTokens) {
        Topic bestPrimary = null;
        Topic bestSecondary = null;

        double highestScore = 0.0;
        double secondHighestScore = 0.0;

        for (Topic topic : knowledgeBase.getAllTopics()) {
            double score = calculateTopicMatchScore(topic, normalizedText, nGrams, filteredTokens);

            if (score > highestScore) {
                secondHighestScore = highestScore;
                bestSecondary = bestPrimary;

                highestScore = score;
                bestPrimary = topic;
            } else if (score > secondHighestScore) {
                secondHighestScore = score;
                bestSecondary = topic;
            }
        }

        // Threshold check
        if (highestScore < 1.5) {
            bestPrimary = null;
        }
        if (secondHighestScore < 1.5) {
            bestSecondary = null;
        }

        return new Topic[]{bestPrimary, bestSecondary};
    }

    private double calculateTopicMatchScore(Topic topic, String normalizedText, List<String> nGrams, List<String> tokens) {
        double score = 0.0;

        double multiplier = "what_is_ml".equals(topic.getId()) ? 0.3 : 1.0;

        String titleLower = topic.getTitle().toLowerCase();
        if (normalizedText.contains(titleLower)) {
            score += 10.0 * multiplier;
        }

        for (String keyword : topic.getKeywords()) {
            if (normalizedText.contains(keyword)) {
                score += 8.0 * multiplier;
                if (normalizedText.equals(keyword) || normalizedText.equals("what is " + keyword)
                        || normalizedText.equals("explain " + keyword)) {
                    score += 5.0 * multiplier;
                }
            }
        }

        for (String nGram : nGrams) {
            for (String keyword : topic.getKeywords()) {
                if (keyword.equalsIgnoreCase(nGram)) {
                    score += 4.0 * multiplier;
                }
            }
        }

        for (String token : tokens) {
            for (String keyword : topic.getKeywords()) {
                if (keyword.contains(token)) {
                    score += multiplier;
                }
            }
        }

        return score;
    }
}