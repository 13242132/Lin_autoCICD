package com.example.ai.flowise.agent.dto;

public class FlowiseRequest {

    private String question;

    public FlowiseRequest(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}