package com.example.testrightnow;

public class TalkItem {
    int no;
    String imgPath;
    String date;
    public TalkItem(int no, String imgPath,String date){
        this.no = no;
        this.imgPath=imgPath;
        this.date=date;
    }
    public int getNo(){
        return no;
    }
    public String getImgPath(){
        return imgPath;
    }
    public void setImgPath(String imgPath){
        this.imgPath=imgPath;
    }
    public String getDate(){
        return date;
    }
    public void setDate(String date){
        this.date=date;
    }
}
