package com.github.ferpinan.twitterbot.command;

import com.github.ferpinan.twitterbot.dto.TelegramUpdate;
import com.github.ferpinan.twitterbot.service.TelegramService;
import com.github.ferpinan.twitterbot.state.State;
import com.github.ferpinan.twitterbot.state.StateEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AwaitTextCommand implements Command{


    private final TelegramService telegramService;

    @Override
    public State execute(TelegramUpdate telegramUpdate, State state) {
        Long chatId = telegramUpdate.getUpdate().getMessage().getChatId();
        telegramService.sendMessage(chatId, "Sartu txioaren textua.");
        state.update(StateEnum.AWAIT_TEXT);
        return state;
    }
}
