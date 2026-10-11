package com.example.chatbot1.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class NLPProcessor {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't",
            "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by",
            "can", "could", "did", "do", "does", "doing", "down", "during", "each", "few", "for", "from",
            "further", "had", "has", "have", "having", "he", "her", "here", "hers", "herself", "him", "himself",
            "his", "how", "i", "if", "in", "into", "is", "isn't", "it", "its", "itself", "just", "me", "more",
            "most", "my", "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "or", "other", "our",
            "ours", "ourselves", "out", "over", "own", "same", "she", "should", "so", "some", "such", "than",
            "that", "the", "their", "theirs", "them", "themselves", "then", "there", "these", "they", "this",
            "those", "through", "to", "too", "under", "until", "up", "very", "was", "we", "were", "what", "when",
            "where", "which", "while", "who", "whom", "why", "will", "with", "would", "you", "your", "yours"
    ));

    // Words that indicate depth or specific requests - should NOT be stripped from analysis
    private static final Set<String> INTENT_PRESERVED_WORDS = new HashSet<>(Arrays.asList(
            "detail", "deep", "deeply", "detailed", "everything", "thoroughly", "vs", "versus", "difference",
            "differ", "compare", "comparison", "example", "examples", "advantage", "advantages", "disadvantage",
            "disadvantages", "limitations", "pro", "pros", "con", "cons", "application", "applications",
            "work", "works", "working", "type", "types", "step", "steps", "process", "it", "its"
    ));

    /**
     * Normalizes input text by converting to lowercase, expanding contractions, handling Hinglish/common phrasing,
     * and removing non-alphanumeric punctuation.
     */
    public String normalize(String rawText) {
        if (rawText == null) return "";

        String text = rawText.toLowerCase(Locale.ROOT).trim();

        // Expand contractions & standard replacements
        text = text.replaceAll("\\bwhat's\\b", "what is");
        text = text.replaceAll("\\bhow's\\b", "how is");
        text = text.replaceAll("\\bcan't\\b", "cannot");
        text = text.replaceAll("\\bdon't\\b", "do not");
        text = text.replaceAll("\\bit's\\b", "it is");
        text = text.replaceAll("\\bml's\\b", "ml");
        text = text.replaceAll("\\bai's\\b", "ai");

        // Hinglish & informal phrase mappings
        text = text.replaceAll("\\bkya hai\\b", "what is");
        text = text.replaceAll("\\bkya hota hai\\b", "what is");
        text = text.replaceAll("\\bbolte hain\\b", "definition");
        text = text.replaceAll("\\bsamjha do\\b", "explain");
        text = text.replaceAll("\\bsamjhao\\b", "explain");
        text = text.replaceAll("\\btell me about\\b", "explain");
        text = text.replaceAll("\\bwhat do you mean by\\b", "explain");
        text = text.replaceAll("\\bcan you explain\\b", "explain");
        text = text.replaceAll("\\bgive me the meaning of\\b", "explain");
        text = text.replaceAll("\\bgive me complete information\\b", "deep explanation");
        text = text.replaceAll("\\btell me everything\\b", "deep explanation");
        text = text.replaceAll("\\bexplain deeply\\b", "deep explanation");
        text = text.replaceAll("\\bdeep explanation\\b", "deep explanation");
        text = text.replaceAll("\\bin detail\\b", "deep explanation");
        text = text.replaceAll("\\bdetailed explanation\\b", "deep explanation");

        // Clean punctuation except spaces and hyphens
        text = text.replaceAll("[^a-z0-9\\s\\-]", " ");
        text = text.replaceAll("\\s+", " ").trim();

        return text;
    }

    /**
     * Splits normalized text into individual tokens.
     */
    public List<String> tokenize(String normalizedText) {
        List<String> tokens = new ArrayList<>();
        if (normalizedText == null || normalizedText.isEmpty()) return tokens;

        String[] parts = normalizedText.split("\\s+");
        for (String p : parts) {
            if (!p.trim().isEmpty()) {
                tokens.add(p.trim());
            }
        }
        return tokens;
    }

    /**
     * Filters out standard stop words, while retaining domain/intent critical terms.
     */
    public List<String> filterStopWords(List<String> tokens) {
        List<String> filtered = new ArrayList<>();
        for (String token : tokens) {
            if (!STOP_WORDS.contains(token) || INTENT_PRESERVED_WORDS.contains(token)) {
                filtered.add(token);
            }
        }
        return filtered;
    }

    /**
     * Rule-based stemming to strip simple suffixes like -ing, -s, -ed, -tion, -ly, -al.
     */
    public String stem(String word) {
        if (word == null || word.length() <= 3) return word;
        String w = word.toLowerCase(Locale.ROOT);

        if (w.endsWith("ingly") && w.length() > 6) return w.substring(0, w.length() - 5);
        if (w.endsWith("ing") && w.length() > 5) return w.substring(0, w.length() - 3);
        if (w.endsWith("ation") && w.length() > 6) return w.substring(0, w.length() - 5) + "ate";
        if (w.endsWith("tion") && w.length() > 5) return w.substring(0, w.length() - 4);
        if (w.endsWith("ness") && w.length() > 5) return w.substring(0, w.length() - 4);
        if (w.endsWith("ies") && w.length() > 4) return w.substring(0, w.length() - 3) + "y";
        if (w.endsWith("es") && w.length() > 4) return w.substring(0, w.length() - 2);
        if (w.endsWith("ed") && w.length() > 4) return w.substring(0, w.length() - 2);
        if (w.endsWith("s") && !w.endsWith("ss") && w.length() > 3) return w.substring(0, w.length() - 1);

        return w;
    }

    /**
     * Extracts multi-word n-grams (bigrams and trigrams) from token list to detect key phrases like "random forest",
     * "supervised learning", "decision tree".
     */
    public List<String> extractNGrams(List<String> tokens) {
        List<String> nGrams = new ArrayList<>();
        int n = tokens.size();

        // Unigrams
        for (String t : tokens) {
            nGrams.add(t);
        }

        // Bigrams
        for (int i = 0; i < n - 1; i++) {
            nGrams.add(tokens.get(i) + " " + tokens.get(i + 1));
        }

        // Trigrams
        for (int i = 0; i < n - 2; i++) {
            nGrams.add(tokens.get(i) + " " + tokens.get(i + 1) + " " + tokens.get(i + 2));
        }

        return nGrams;
    }
}