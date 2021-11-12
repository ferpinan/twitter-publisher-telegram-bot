package com.github.ferpinan.twitterbot.state;

public enum StateEnum {
    NOT_STARTED,
    STARTED,
    PASSWORD_OK,
    PASSWORD_KO,
    AWAIT_COMMAND,
    AWAIT_TEXT,
    AWAIT_GIF,
    AWAIT_PHOTOS,
    FINISHED
}
