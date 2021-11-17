package com.github.ferpinan.twitterbot.state;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class State {

    public State() {
        currentState = StateEnum.NOT_STARTED;
        message = null;
        gif = null;
        photos = new ArrayList<>();
    }

    private StateEnum currentState;
    private String message;
    private File gif;
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

    public void setGif(File gif){
        this.gif = gif;
    }

    public File getGif() {
        return gif;
    }

    public void addPhoto(File document){
        photos.add(document);
    }

    public List<File> getPhotos() {
        return photos;
    }

    public StateEnum getCurrentState() {
        return currentState;
    }
}
