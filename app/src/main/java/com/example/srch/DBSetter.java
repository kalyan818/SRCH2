package com.example.srch;

public class DBSetter {
    String Images;
    String Text;
    public DBSetter(){

    }
    public DBSetter(String Image,String Text){
        this.Images = Image;
        this.Text = Text;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getImages() {
        return Images;
    }

    public void setImages(String images) {
        Images = images;
    }
}
