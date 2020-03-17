package com.example.scoringsystem.bean.QuestionBean;

public class QuestionBean {
    private int id_q;
    private String text;
    private String subject;

    public void setId_q(int id_q) {
        this.id_q = id_q;
    }

    public int getId_q() {
        return id_q;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }
}
