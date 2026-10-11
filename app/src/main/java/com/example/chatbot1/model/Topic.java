package com.example.chatbot1.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Topic {

    private String id;
    private String title;
    private String category;
    private List<String> keywords;
    private String shortAnswer;
    private String normalAnswer;
    private String working;
    private String deepAnswer;
    private List<String> examples;
    private List<String> advantages;
    private List<String> limitations;
    private List<String> applications;
    private List<String> relatedTopics;
    private Map<String, String> comparisons; // targetTopicId -> Comparison detail string

    public Topic(String id, String title, String category) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.keywords = new ArrayList<>();
        this.examples = new ArrayList<>();
        this.advantages = new ArrayList<>();
        this.limitations = new ArrayList<>();
        this.applications = new ArrayList<>();
        this.relatedTopics = new ArrayList<>();
        this.comparisons = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void addKeyword(String keyword) {
        if (!this.keywords.contains(keyword.toLowerCase())) {
            this.keywords.add(keyword.toLowerCase());
        }
    }

    public String getShortAnswer() {
        return shortAnswer;
    }

    public void setShortAnswer(String shortAnswer) {
        this.shortAnswer = shortAnswer;
    }

    public String getNormalAnswer() {
        return normalAnswer;
    }

    public void setNormalAnswer(String normalAnswer) {
        this.normalAnswer = normalAnswer;
    }

    public String getWorking() {
        return working;
    }

    public void setWorking(String working) {
        this.working = working;
    }

    public String getDeepAnswer() {
        return deepAnswer;
    }

    public void setDeepAnswer(String deepAnswer) {
        this.deepAnswer = deepAnswer;
    }

    public List<String> getExamples() {
        return examples;
    }

    public void addExample(String example) {
        this.examples.add(example);
    }

    public List<String> getAdvantages() {
        return advantages;
    }

    public void addAdvantage(String advantage) {
        this.advantages.add(advantage);
    }

    public List<String> getLimitations() {
        return limitations;
    }

    public void addLimitation(String limitation) {
        this.limitations.add(limitation);
    }

    public List<String> getApplications() {
        return applications;
    }

    public void addApplication(String application) {
        this.applications.add(application);
    }

    public List<String> getRelatedTopics() {
        return relatedTopics;
    }

    public void addRelatedTopic(String topicId) {
        if (!this.relatedTopics.contains(topicId)) {
            this.relatedTopics.add(topicId);
        }
    }

    public Map<String, String> getComparisons() {
        return comparisons;
    }

    public void addComparison(String otherTopicId, String comparisonText) {
        this.comparisons.put(otherTopicId, comparisonText);
    }
}