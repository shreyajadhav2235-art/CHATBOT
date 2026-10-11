package com.example.chatbot1.multimodal;

import com.example.chatbot1.model.ChatMessage;
import com.example.chatbot1.nlp.ConversationManager;
import com.example.chatbot1.nlp.QueryAnalyzer;
import com.example.chatbot1.nlp.ResponseManager;
import com.example.chatbot1.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MultimodalProcessor {

    private final QueryAnalyzer queryAnalyzer;
    private final ResponseManager responseManager;

    public MultimodalProcessor(QueryAnalyzer queryAnalyzer, ResponseManager responseManager) {
        this.queryAnalyzer = queryAnalyzer;
        this.responseManager = responseManager;
    }

    public ChatMessage processQuery(String userPrompt, Attachment attachment, ConversationManager conversationManager) {
        String currentTime = Utils.getCurrentTimestamp();

        // 1. If attachment is present
        if (attachment != null) {
            // Check if OCR / Text extraction succeeded
            if (!attachment.hasExtractedText()) {
                String errorMsg;
                if (attachment.getType() == Attachment.Type.IMAGE) {
                    errorMsg = "Sorry, I couldn't clearly read the text in this image. Please upload a clearer image or photo with legible Machine Learning text.";
                } else {
                    errorMsg = "I couldn't identify readable text in the uploaded file. Please upload a document or PDF with clear text content.";
                }
                return new ChatMessage(ChatMessage.TYPE_BOT, errorMsg, currentTime, "ERROR", attachment.getFileName());
            }

            String extractedText = attachment.getExtractedText().trim();

            // Check for specific ML questions inside the extracted text
            String combinedQuery = buildCombinedQuery(userPrompt, extractedText);

            // Run NLP query analyzer on extracted text + user question
            QueryAnalyzer.AnalysisResult analysis = queryAnalyzer.analyze(combinedQuery, conversationManager);

            // If analysis matched an ML topic, return the expert topic response
            if (analysis.matchedTopic != null) {
                return responseManager.generateResponse(analysis, conversationManager);
            }

            // Also check if extracted text alone matches an ML topic
            QueryAnalyzer.AnalysisResult extractedOnlyAnalysis = queryAnalyzer.analyze(extractedText, conversationManager);
            if (extractedOnlyAnalysis.matchedTopic != null) {
                return responseManager.generateResponse(extractedOnlyAnalysis, conversationManager);
            }

            // Fallback: Generate structured analysis / answer for extracted image or document content
            String responseText = buildDocumentAnalysisResponse(userPrompt, extractedText, attachment.getType());
            ChatMessage botMsg = new ChatMessage(ChatMessage.TYPE_BOT, responseText, currentTime, "IMAGE_ANALYSIS", attachment.getFileName());
            List<String> suggestions = new ArrayList<>();
            suggestions.add("Summarize this content");
            suggestions.add("Extract key points");
            botMsg.setSuggestedQuestions(suggestions);
            return botMsg;
        }

        // 2. Standard text / voice prompt processing using existing NLP pipeline
        QueryAnalyzer.AnalysisResult analysis = queryAnalyzer.analyze(userPrompt, conversationManager);
        return responseManager.generateResponse(analysis, conversationManager);
    }

    private String buildCombinedQuery(String userPrompt, String extractedText) {
        if (userPrompt == null || userPrompt.trim().isEmpty() || isGenericPrompt(userPrompt)) {
            return extractedText;
        }
        return userPrompt + " " + extractedText;
    }

    private boolean isGenericPrompt(String prompt) {
        String lower = prompt.toLowerCase().trim();
        return lower.contains("give me the answer") || lower.contains("answer this")
                || lower.contains("explain this") || lower.contains("what is this")
                || lower.contains("solution") || lower.contains("solve")
                || lower.equals("answer") || lower.equals("explain");
    }

    private String buildDocumentAnalysisResponse(String userPrompt, String extractedText, Attachment.Type type) {
        StringBuilder sb = new StringBuilder();
        String promptLower = userPrompt != null ? userPrompt.toLowerCase() : "";

        if (type == Attachment.Type.IMAGE) {
            sb.append("### Image Text Analysis\n\n");
            if (userPrompt != null && !userPrompt.trim().isEmpty() && !isGenericPrompt(userPrompt)) {
                sb.append("**Question**: ").append(userPrompt).append("\n\n");
            }
            sb.append("**Extracted Text from Image**:\n\n");
            sb.append(extractedText).append("\n\n");
            sb.append("**Answer / Overview**:\n");
            sb.append("• ").append(extractedText.replace("\n", "\n• "));
        } else if (promptLower.contains("summarize") || promptLower.contains("summary")) {
            sb.append("### Summary of ").append(type.name()).append(" Content\n\n");
            sb.append("Based on the uploaded ").append(type.name().toLowerCase()).append(" content:\n\n");

            String[] lines = extractedText.split("\n");
            int pointCount = 0;
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.length() > 15 && !trimmed.startsWith("---")) {
                    sb.append("• ").append(trimmed).append("\n");
                    pointCount++;
                    if (pointCount >= 6) break;
                }
            }
            if (pointCount == 0) {
                sb.append("• ").append(extractedText.length() > 300 ? extractedText.substring(0, 300) + "..." : extractedText).append("\n");
            }
        } else {
            sb.append("### Analysis of Uploaded ").append(type.name()).append("\n\n");
            if (userPrompt != null && !userPrompt.trim().isEmpty() && !isGenericPrompt(userPrompt)) {
                sb.append("**Question**: ").append(userPrompt).append("\n\n");
            }
            sb.append("**Extracted Content**:\n\n");
            sb.append(extractedText);
        }

        return sb.toString();
    }
}