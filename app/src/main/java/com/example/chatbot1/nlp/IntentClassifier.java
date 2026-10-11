package com.example.chatbot1.nlp;

import java.util.List;

public class IntentClassifier {

    public enum Intent {
        GREETING,
        THANK_YOU,
        ACKNOWLEDGEMENT,
        FAREWELL,
        HELP,
        DEFINITION,
        ML_TYPES,
        WORKING,
        ADVANTAGES,
        LIMITATIONS,
        EXAMPLES,
        APPLICATIONS,
        DEEP_EXPLANATION,
        COMPARISON,
        FOLLOW_UP,
        EXPLANATION
    }

    /**
     * Classifies user intent based on normalized text and extracted tokens/nGrams.
     */
    public Intent classifyIntent(String normalizedText, List<String> nGrams) {
        if (normalizedText == null || normalizedText.trim().isEmpty()) {
            return Intent.EXPLANATION;
        }

        String text = normalizedText.trim();

        // 1. Pure Conversational / Social Messages (ONLY when strictly social)
        if (isPureGreeting(text)) {
            return Intent.GREETING;
        }
        if (isPureThankYou(text)) {
            return Intent.THANK_YOU;
        }
        if (isPureAcknowledgement(text)) {
            return Intent.ACKNOWLEDGEMENT;
        }
        if (isPureFarewell(text)) {
            return Intent.FAREWELL;
        }
        if (isHelp(text)) {
            return Intent.HELP;
        }

        // 2. Types of Machine Learning explicit query
        if (isMlTypes(text)) {
            return Intent.ML_TYPES;
        }

        // 3. Comparison detection
        if (isComparison(text, nGrams)) {
            return Intent.COMPARISON;
        }

        // 4. Deep explanation cues
        if (isDeepExplanation(text)) {
            return Intent.DEEP_EXPLANATION;
        }

        // 5. Working / Mechanism
        if (isWorkingRequest(text)) {
            return Intent.WORKING;
        }

        // 6. Advantages
        if (isAdvantagesRequest(text)) {
            return Intent.ADVANTAGES;
        }

        // 7. Limitations / Disadvantages
        if (isLimitationsRequest(text)) {
            return Intent.LIMITATIONS;
        }

        // 8. Examples
        if (isExamplesRequest(text)) {
            return Intent.EXAMPLES;
        }

        // 9. Applications / Uses
        if (isApplicationsRequest(text)) {
            return Intent.APPLICATIONS;
        }

        // 10. Follow-up cues
        if (isFollowUp(text)) {
            return Intent.FOLLOW_UP;
        }

        // 11. Definition request
        if (isDefinition(text)) {
            return Intent.DEFINITION;
        }

        return Intent.EXPLANATION;
    }

    public boolean isPureGreeting(String text) {
        if (text == null) return false;
        String clean = text.replaceAll("(.)\\1{2,}", "$1$1");
        return clean.matches("^(h+i+|h+e+l+o+|h+e+y+|greetings|good\\s*morning|good\\s*afternoon|good\\s*evening|namaste|hallo|hola)((\\s+)(there|bot|assistant|friend|all|everyone))?$");
    }

    public boolean isPureThankYou(String text) {
        if (text == null) return false;
        String clean = text.replaceAll("(.)\\1{2,}", "$1$1");
        return clean.matches("^(thank(s|\\s*you|\\s*u)?|thx|thnx|tq|thankyou)((\\s+)(very\\s*much|a\\s*lot|bot|assistant))?$");
    }

    public boolean isPureAcknowledgement(String text) {
        if (text == null) return false;
        String clean = text.replaceAll("(.)\\1{2,}", "$1$1");
        return clean.matches("^(o+k+(a+y+)?|got\\s*it|understood|alright|sure|cool|fine|kk)((\\s+)(bot|assistant))?$");
    }

    public boolean isPureFarewell(String text) {
        if (text == null) return false;
        String clean = text.replaceAll("(.)\\1{2,}", "$1$1");
        return clean.matches("^(b+y+e*|good\\s*bye|cya|see\\s*ya|take\\s*care)((\\s+)(bot|assistant|all))?$");
    }

    private boolean isHelp(String text) {
        return text.contains("what can you do") || text.contains("who are you") || text.equals("help")
                || text.contains("what do you know") || text.contains("how to use");
    }

    private boolean isMlTypes(String text) {
        return text.contains("types of machine learning") || text.contains("types of ml")
                || text.contains("categories of machine learning") || text.contains("categories of ml")
                || text.contains("different types of machine learning") || text.contains("different types of ml")
                || text.contains("how many types of machine learning") || text.contains("how many types of ml")
                || text.contains("explain different types of ml") || text.contains("explain types of ml");
    }

    private boolean isComparison(String text, List<String> nGrams) {
        if (text.contains(" vs ") || text.contains(" versus ") || text.contains("difference between")
                || text.contains("differ from") || text.contains("compare ") || text.contains("comparison")
                || text.contains("different from")) {
            return true;
        }
        if (nGrams != null) {
            for (String gram : nGrams) {
                if (gram.contains(" vs ") || gram.contains(" versus ")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isDeepExplanation(String text) {
        return text.contains("deep explanation") || text.contains("explain deeply") || text.contains("in detail")
                || text.contains("detailed explanation") || text.contains("complete information")
                || text.contains("tell me everything") || text.contains("thoroughly") || text.contains("comprehensive")
                || text.contains("advantages and disadvantages") || text.contains("pros and cons");
    }

    private boolean isWorkingRequest(String text) {
        return text.contains("how does it work") || text.contains("how it works") || text.contains("how does")
                || text.contains("working of") || text.contains("mechanism") || text.contains("step by step process");
    }

    private boolean isAdvantagesRequest(String text) {
        return text.contains("advantage") || text.contains("advantages") || text.contains("benefit")
                || text.contains("benefits") || text.contains("pros") || text.contains("merits");
    }

    private boolean isLimitationsRequest(String text) {
        return text.contains("limitation") || text.contains("limitations") || text.contains("disadvantage")
                || text.contains("disadvantages") || text.contains("drawback") || text.contains("drawbacks")
                || text.contains("cons") || text.contains("demerits");
    }

    private boolean isExamplesRequest(String text) {
        return text.contains("example") || text.contains("examples") || text.contains("give me an example")
                || text.contains("practical example") || text.contains("real world example");
    }

    private boolean isApplicationsRequest(String text) {
        return text.contains("application") || text.contains("applications") || text.contains("uses")
                || text.contains("used for") || text.contains("where is it used") || text.contains("where to use");
    }

    private boolean isFollowUp(String text) {
        return text.matches(".*\\b(it|its|this|that|they|them|the algorithm|the model)\\b.*")
                || text.equals("how does it work") || text.equals("explain it deeply")
                || text.equals("what are its advantages") || text.equals("what are its limitations")
                || text.equals("give me an example") || text.equals("what are its types")
                || text.equals("explain it") || text.equals("explain it in detail")
                || text.startsWith("and ") || text.startsWith("what about ");
    }

    private boolean isDefinition(String text) {
        return text.startsWith("what is") || text.startsWith("define") || text.startsWith("meaning of")
                || text.startsWith("definition of") || text.startsWith("what do you mean by");
    }
}