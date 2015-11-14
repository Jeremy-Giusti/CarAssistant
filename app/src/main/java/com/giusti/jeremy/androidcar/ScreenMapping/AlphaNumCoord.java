package com.giusti.jeremy.androidcar.ScreenMapping;

/**
 * Created by jgiusti on 20/10/2015.
 */
public class AlphaNumCoord {
    private int num;
    private char alpha;
    int asciiValueA = (int) 'A';

    public AlphaNumCoord(int num, char alpha) {
        this.num = num;
        this.alpha = Character.toUpperCase(alpha);
    }

    public AlphaNumCoord(int num, int alphavalue) {
        this.num = num;
        this.setAlphaAsValue(alphavalue);
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public char getAlpha() {
        return alpha;
    }

    public void setAlpha(char alpha) {
        this.alpha = Character.toUpperCase(alpha);
    }

    public int getAlphaAsValue(){
        return (((int)alpha) - asciiValueA);
    }

    public void setAlphaAsValue(int alphaValue){
        this.alpha=(char) (alphaValue+asciiValueA);
    }

    @Override
    public String toString() {
        return ""+alpha+num;
    }
}
