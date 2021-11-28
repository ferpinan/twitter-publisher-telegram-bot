package com.github.ferpinan.twitterbot.command;

import com.github.ferpinan.twitterbot.dto.TelegramUpdate;
import com.github.ferpinan.twitterbot.service.TelegramService;
import com.github.ferpinan.twitterbot.state.State;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotStartedCommand implements Command{

    private final TelegramService telegramService;

    @Override
    public State execute(TelegramUpdate telegramUpdate, State state) {
        Long chatId = telegramUpdate.getUpdate().getMessage().getChatId();
        telegramService.sendMessage(chatId, "Hasteko \"/hasi\" komandoa idatzi behar duzu.");
        return state;
    }
}
