package com.example.mydictionaryapp;

public class Data {
    int aj, bj, mj;
    String[][] subArray;
    Data(int aj, int bj, int mj){
        this.aj = aj;
        this.bj = bj;
        this.mj = mj;
    }
    void setSubArray(){
        subArray = new String[mj][2];
    }
}
