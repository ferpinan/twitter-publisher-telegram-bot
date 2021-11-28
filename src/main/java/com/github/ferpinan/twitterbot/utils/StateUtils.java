package com.github.ferpinan.twitterbot.utils;

import com.github.ferpinan.twitterbot.state.State;

public class StateUtils {

    private StateUtils(){ throw new IllegalStateException("Utility class");}

    public static boolean isPhotosAttached(State state) {
        return state.getPhotos() != null && !state.getPhotos().isEmpty();
    }

    public static boolean isGifAttached(State state) {
        return state.getGif() != null;
    }

    public static boolean isVideoAttached(State state) {
        return state.getVideo() != null;
    }
}
