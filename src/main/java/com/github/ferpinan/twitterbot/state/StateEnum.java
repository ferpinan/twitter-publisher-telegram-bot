package com.github.ferpinan.twitterbot.state;

public enum StateEnum {
    NOT_STARTED,
    STARTED,
    AWAIT_PASSWORD,
    PASSWORD_KO,
    AWAIT_COMMAND,
    AWAIT_TEXT,
    AWAIT_GIF,
    AWAIT_PHOTOS,
    RECEIVED_TEXT_COMMAND,
    RECEIVED_GIF_COMMAND,
    RECEIVED_PHOTOS_COMMAND,
    RECEIVED_FINISHED
}
