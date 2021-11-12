package com.github.ferpinan.twitterbot.utils;

import com.github.ferpinan.twitterbot.state.State;

public class StateUtils {

    public static boolean isPhotosAttached(State state) {
        return state.getPhotos() != null && !state.getPhotos().isEmpty();
    }

    public static boolean isGifAttached(State state) {
        return state.getDocument() != null;
    }
}
