package com.github.ferpinan.twitterbot.state;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class State {

    public State() {
        currentState = StateEnum.NOT_STARTED;
        message = null;
        document = null;
        photos = new ArrayList<>();
    }

    private StateEnum currentState;
    private String message;
    private File document;
    private List<File> photos;

    public void update(StateEnum newState){
        currentState = newState;
    }

    public boolean is(StateEnum stateEnum){
        return currentState.equals(stateEnum);
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setDocument(File document){
        this.document = document;
    }

    public File getDocument() {
        return document;
    }

    public void addPhoto(File document){
        photos.add(document);
    }

    public List<File> getPhotos() {
        return photos;
    }
}
