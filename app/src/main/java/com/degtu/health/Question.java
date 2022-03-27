package com.degtu.health;

public class Question {

    private String title,question;

    public Question() {
    }

    public Question(String title, String question) {
        this.title = title;
        this.question = question;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
