package com.example.scoringsystem.bean.OCRBean;

import java.util.List;

public class Line {
    private int confidence;
    private List<Word> word;

    public Line(int confidence, List<Word> word) {
        this.confidence = confidence;
        this.word = word;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public List<Word> getWord() {
        return word;
    }

    public void setWord(List<Word> word) {
        this.word = word;
    }
}
