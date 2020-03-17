package com.example.scoringsystem.bean.OCRBean;

import java.util.List;

public class Data {
    private List<Block> block;

    public Data(List<Block> block) {
        this.block = block;
    }

    public List<Block> getBlock() {
        return block;
    }

    public void setBlock(List<Block> block) {
        this.block = block;
    }
}
