package com.example.scoringsystem.bean.SimilarityBean;

import java.util.List;

public class SentSimilarityBean {
    private String status;
    private double similarity;
    private List<Most_similarity> most_similarity;

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setMost_similarity(List<Most_similarity> most_similarity) {
        this.most_similarity = most_similarity;
    }

    public List<Most_similarity> getMost_similarity() {
        return most_similarity;
    }
}
