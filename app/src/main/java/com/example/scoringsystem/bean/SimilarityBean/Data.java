package com.example.scoringsystem.bean.SimilarityBean;

public class Data {
    private Text texts;
    private String score;

    public Data(Text texts, String score) {
        this.texts = texts;
        this.score = score;
    }

    public Text getTexts() {
        return texts;
    }

    public void setTexts(Text texts) {
        this.texts = texts;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
