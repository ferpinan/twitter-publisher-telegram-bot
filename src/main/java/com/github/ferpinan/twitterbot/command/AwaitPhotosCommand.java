package com.github.ferpinan.twitterbot.command;

import com.github.ferpinan.twitterbot.service.TelegramService;
import com.github.ferpinan.twitterbot.state.State;
import com.github.ferpinan.twitterbot.state.StateEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class AwaitPhotosCommand implements Command{


    private final TelegramService telegramService;

    @Override
    public void execute(Update update, State state) {
        Long chatId = update.getMessage().getChatId();
        telegramService.sendMessage(chatId, "Bidali gehienez 4 argazki.");
        state.update(StateEnum.AWAIT_PHOTOS);
    }
}
