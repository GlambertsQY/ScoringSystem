package com.example.scoringsystem.bean;

public class QuestionStandardAnswerBean {
    private int id_q;
    private String title;
    private String subject;
    private String standardanswer;

    public void setId_q(int id_q) {
        this.id_q = id_q;
    }

    public int getId_q() {
        return id_q;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setStandardanswer(String standardanswer) {
        this.standardanswer = standardanswer;
    }

    public String getStandardanswer() {
        return standardanswer;
    }
}
