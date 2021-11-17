package com.github.ferpinan.twitterbot.command;

import com.github.ferpinan.twitterbot.service.TelegramService;
import com.github.ferpinan.twitterbot.service.TwitterService;
import com.github.ferpinan.twitterbot.state.State;
import com.github.ferpinan.twitterbot.state.StateEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import twitter4j.TwitterException;

import java.io.FileNotFoundException;

@Component
@RequiredArgsConstructor
public class FinishCommand implements Command{

    private final TelegramService telegramService;
    private final TwitterService twitterService;

    @Override
    public State execute(Update update, State state) {
        Long chatId = update.getMessage().getChatId();
        telegramService.sendMessage(chatId, "Mezua txiokatu da!");

        try {
            twitterService.tweet(state);
        } catch (FileNotFoundException | TwitterException e) {
            e.printStackTrace();
        }

        state.update(StateEnum.FINISHED);
        return state;
    }
}
