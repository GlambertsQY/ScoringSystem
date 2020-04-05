package com.example.scoringsystem.bean;

public class AnswerBean {
    private int id_q;
    private int id_a;
    private String title;
    private String standardanswer;
    private String answer;
    private int score;
    private String score_time;

    public void setId_q(int id_q) {
        this.id_q = id_q;
    }

    public int getId_q() {
        return id_q;
    }

    public void setId_a(int id_a) {
        this.id_a = id_a;
    }

    public int getId_a() {
        return id_a;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setStandardanswer(String standardanswer) {
        this.standardanswer = standardanswer;
    }

    public String getStandardanswer() {
        return standardanswer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setScore_time(String score_time) {
        this.score_time = score_time;
    }

    public String getScore_time() {
        return score_time;
    }
}
