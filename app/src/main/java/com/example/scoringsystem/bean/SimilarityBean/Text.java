package com.example.scoringsystem.bean.SimilarityBean;

public class Text {
    private String text_2;
    private String text_1;

    public String getText_2() {
        return text_2;
    }

    public void setText_2(String text_2) {
        this.text_2 = text_2;
    }

    public String getText_1() {
        return text_1;
    }

    public void setText_1(String text_1) {
        this.text_1 = text_1;
    }

    public Text(String text_2, String text_1) {
        this.text_2 = text_2;
        this.text_1 = text_1;
    }
}
