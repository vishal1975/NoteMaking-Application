package com.example.note_taking_application.model;

import java.util.List;

public class Note {
    private String title;
    private String content;
    String titleSalt;
    String titleiv;
    String contentSalt;
    String contentiv;
    public Note(){}
    public Note(String title,String content,String titleSalt,String titleiv,String contentSalt,String contentiv){
        this.title = title;
        this.content = content;
        this.titleSalt=titleSalt;
        this.titleiv=titleiv;
        this.contentSalt=contentSalt;
        this.contentiv=contentiv;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitleSalt() {
        return titleSalt;
    }

    public void setTitleSalt(String titleSalt) {
        this.titleSalt = titleSalt;
    }

    public String getTitleiv() {
        return titleiv;
    }

    public void setTitleiv(String titleiv) {
        this.titleiv = titleiv;
    }

    public String getContentSalt() {
        return contentSalt;
    }

    public void setContentSalt(String contentSalt) {
        this.contentSalt = contentSalt;
    }

    public String getContentiv() {
        return contentiv;
    }

    public void setContentiv(String contentiv) {
        this.contentiv = contentiv;
    }
}