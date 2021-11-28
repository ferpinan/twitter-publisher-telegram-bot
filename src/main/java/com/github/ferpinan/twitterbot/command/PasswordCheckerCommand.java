package com.github.ferpinan.twitterbot.command;

import com.github.ferpinan.twitterbot.dto.TelegramUpdate;
import com.github.ferpinan.twitterbot.factory.MessageFactory;
import com.github.ferpinan.twitterbot.service.TelegramService;
import com.github.ferpinan.twitterbot.state.State;
import com.github.ferpinan.twitterbot.state.StateEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordCheckerCommand implements Command {

    private final TelegramService telegramService;
    private final MessageFactory messageFactory;

    @Value("${telegram.bot.password}")
    private String pasahitza;

    @Override
    public State execute(TelegramUpdate telegramUpdate, State state) {
        Long chatId = telegramUpdate.getUpdate().getMessage().getChatId();
        String message = telegramUpdate.getUpdate().getMessage().getText();
        if (pasahitza.equals(message)) {
            telegramService.sendMessage(chatId, "Pasahitza zuzena da!");
            telegramService.sendMessage(chatId, messageFactory.zerGehitu(state));
            state.update(StateEnum.AWAIT_COMMAND);
        } else {
            telegramService.sendMessage(chatId, "Pasahitza ez zuzena da!");
            state.update(StateEnum.PASSWORD_KO);
        }
        return state;
    }
}
