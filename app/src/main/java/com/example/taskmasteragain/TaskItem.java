package com.example.taskmasteragain;

public class TaskItem {
    private  int image;
    private final String title;
    private final String body;
    private final String state;


    public TaskItem(int image, String title, String body, String state) {
        this.image = image;
        this.title = title;
        this.body = body;
        this.state = state;
    }

    public TaskItem(String title, String body, String state) {
        this.title = title;
        this.body = body;
        this.state = state;
    }

    public int getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getState() {
        return state;
    }
}
