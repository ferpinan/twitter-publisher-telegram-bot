package com.github.ferpinan.twitterbot.command;

import com.github.ferpinan.twitterbot.dto.TelegramUpdate;
import com.github.ferpinan.twitterbot.service.TelegramService;
import com.github.ferpinan.twitterbot.service.TwitterService;
import com.github.ferpinan.twitterbot.state.State;
import com.github.ferpinan.twitterbot.state.StateEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import twitter4j.TwitterException;

import java.io.FileNotFoundException;

@Component
@RequiredArgsConstructor
@Log4j2
public class FinishCommand implements Command{

    private final TelegramService telegramService;
    private final TwitterService twitterService;

    @Override
    public State execute(TelegramUpdate telegramUpdate, State state) {
        Long chatId = telegramUpdate.getUpdate().getMessage().getChatId();

        try {
            twitterService.tweet(state);
            telegramService.sendMessage(chatId, "Mezua txiokatu da!");
        } catch (FileNotFoundException | TwitterException e) {
            log.error("Errore bat egrtatu da:");
            log.error(e.getMessage());
            telegramService.sendMessage(chatId, "Mezua txiokatzerakoan errore bat egon da!");
        }

        return new State();
    }
}
