package com.github.ferpinan.twitterbot.command;

import com.github.ferpinan.twitterbot.dto.TelegramUpdate;
import com.github.ferpinan.twitterbot.state.State;

public interface Command {
    State execute(TelegramUpdate update, State state);
}
