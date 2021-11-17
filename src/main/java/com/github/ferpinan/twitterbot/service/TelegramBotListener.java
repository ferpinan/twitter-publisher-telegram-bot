package com.github.ferpinan.twitterbot.service;

import com.github.ferpinan.twitterbot.dto.TelegramUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TelegramBotListener extends TelegramLongPollingBot {

    private final CommandDispatcher commandDispatcher;

    @Value("${telegram.bot.username}")
    private String botUsername;
    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public void onUpdateReceived(final Update update) {
        TelegramUpdate telegramUpdate = (TelegramUpdate)update;
        telegramUpdate.setIsLastUpdate(true);
        commandDispatcher.mainMethod(telegramUpdate);
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        List<TelegramUpdate> telegramUpdates = updates.stream().map(update -> {
            TelegramUpdate telegramUpdate = (TelegramUpdate) update;
            telegramUpdate.setIsLastUpdate(false);
            return telegramUpdate;
        }).toList();
        telegramUpdates.get(telegramUpdates.size()-1).setIsLastUpdate(true);
        for(TelegramUpdate telegramUpdate: telegramUpdates) {
            commandDispatcher.mainMethod(telegramUpdate);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
