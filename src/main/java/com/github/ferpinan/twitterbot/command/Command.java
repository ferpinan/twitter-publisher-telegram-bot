package com.github.ferpinan.twitterbot.command;

import com.github.ferpinan.twitterbot.state.State;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {
    State execute(Update update, State state);
}
