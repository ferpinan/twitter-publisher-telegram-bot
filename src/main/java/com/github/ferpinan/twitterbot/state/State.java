package com.github.ferpinan.twitterbot.state;

import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
public class State {

    public State() {
        currentState = StateEnum.NOT_STARTED;
        message = null;
        gif = null;
        photos = new ArrayList<>();
        video = null;
    }

    private StateEnum currentState;
    private String message;
    private File gif;
    private List<File> photos;
    private File video;

    public void update(StateEnum newState){
        currentState = newState;
    }

    public boolean is(StateEnum stateEnum){
        return currentState.equals(stateEnum);
    }
    public boolean isNot(StateEnum stateEnum){
        return !this.is(stateEnum);
    }

    public void addPhoto(File document){
        photos.add(document);
    }

    public boolean photoExists(File newFile){
        return photos.stream().anyMatch(file -> file.getName().equals(newFile.getName()));
    }
}
