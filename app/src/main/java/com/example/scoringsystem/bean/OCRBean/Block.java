package com.example.scoringsystem.bean.OCRBean;

import java.util.List;

public class Block {
    private String type;
    private List<Line> line;

    public Block(String type, List<Line> line) {
        this.type = type;
        this.line = line;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Line> getLine() {
        return line;
    }

    public void setLine(List<Line> line) {
        this.line = line;
    }
}
