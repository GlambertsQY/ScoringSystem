package com.example.scoringsystem.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Answer {
    private String title;
    private String standardAnswer;
    private String answer;
    private int score;
    private String score_time;

    public Answer(String title, String standardAnswer, String answer, int score, String score_time) {
        this.title = title;
        this.standardAnswer = standardAnswer;
        this.answer = answer;
        this.score = score;
        this.score_time = score_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStandardAnswer() {
        return standardAnswer;
    }

    public void setStandardAnswer(String standardAnswer) {
        this.standardAnswer = standardAnswer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getScore_time() {
        return score_time;
    }

    public void setScore_time(String score_time) {
        this.score_time = score_time;
    }
}
