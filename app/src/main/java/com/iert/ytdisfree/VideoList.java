package com.iert.ytdisfree;

public class VideoList {
    String title, url, documentId;

    public VideoList(){}
    public VideoList(String title, String url, String documentId) {
        this.title = title;
        this.url = url;
        this.documentId = documentId;
    }


    public String getDocumentId() {
        return documentId;
    }

   public void  setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
