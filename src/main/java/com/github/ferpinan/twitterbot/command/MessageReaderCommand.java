package com.github.ferpinan.twitterbot.command;

import com.github.ferpinan.twitterbot.factory.MessageFactory;
import com.github.ferpinan.twitterbot.service.TelegramService;
import com.github.ferpinan.twitterbot.state.State;
import com.github.ferpinan.twitterbot.state.StateEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class MessageReaderCommand implements Command{

    private final TelegramService telegramService;
    private final MessageFactory messageFactory;

    @Override
    public void execute(Update update, State state) {
        Long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();
        state.setMessage(message);
        telegramService.sendMessage(chatId, "Gorde da mezua.");
        telegramService.sendMessage(chatId, messageFactory.zerGehitu(state));
        state.update(StateEnum.AWAIT_COMMAND);
    }
}
