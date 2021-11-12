package com.github.ferpinan.twitterbot.command;

import com.github.ferpinan.twitterbot.factory.MessageFactory;
import com.github.ferpinan.twitterbot.service.TelegramService;
import com.github.ferpinan.twitterbot.state.State;
import com.github.ferpinan.twitterbot.state.StateEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import twitter4j.TwitterException;

import java.io.File;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class DocumentReaderCommand implements Command{

    private final TelegramService telegramService;
    private final MessageFactory messageFactory;

    @Override
    public void execute(Update update, State state) {
        Long chatId = update.getMessage().getChatId();

        Document document1 = update.getMessage().getDocument();
        try {
            File file1 = telegramService.downloadFile(document1.getFileId());
            state.setDocument(file1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        telegramService.sendMessage(chatId, "Gorde da mezua.");
        telegramService.sendMessage(chatId, messageFactory.zerGehitu(state));
        state.update(StateEnum.AWAIT_COMMAND);
    }
}
