package com.github.ferpinan.twitterbot.service;

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
        commandDispatcher.mainMethod(update);
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        for(Update update: updates) {
            commandDispatcher.mainMethod(update);
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
