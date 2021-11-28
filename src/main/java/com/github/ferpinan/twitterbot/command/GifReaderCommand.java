package com.github.ferpinan.twitterbot.command;

import com.github.ferpinan.twitterbot.dto.TelegramUpdate;
import com.github.ferpinan.twitterbot.factory.MessageFactory;
import com.github.ferpinan.twitterbot.service.TelegramService;
import com.github.ferpinan.twitterbot.state.State;
import com.github.ferpinan.twitterbot.state.StateEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Document;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Log4j2
public class GifReaderCommand implements Command{

    protected static final String GIF_SAVED = "Gorde da gif-a.";
    protected static final String GIF_ALREADY_UPLOADED = "Dagoeneko igo duzu gif bat.";
    protected static final String GIF_NOT_RECEIVED = "Ez da gifik jaso.";
    protected static final String PHOTOS_ALREADY_UPLOADED = "Dagoeneko igo dituzu argazkiak eta ezin da gif-ik txertatu";
    protected static final String ERROR = "Error bat gertatu da gif-a txertatzerakoan";

    private final TelegramService telegramService;
    private final MessageFactory messageFactory;

    @Override
    public State execute(TelegramUpdate update, State state) {
        Long chatId = update.getMessage().getChatId();
        Document gifDocument = update.getMessage().getDocument();

        if(gifDocument==null || StringUtils.isEmpty(gifDocument.getFileId())){
            return finishCommand(state, chatId, GIF_NOT_RECEIVED);
        }
        if(Objects.nonNull(state.getGif())){
            return finishCommand(state, chatId, GIF_ALREADY_UPLOADED);
        }
        if(Objects.nonNull(state.getPhotos()) && !state.getPhotos().isEmpty()){
            return finishCommand(state, chatId, PHOTOS_ALREADY_UPLOADED);
        }

        try {
            File gifFile = telegramService.downloadFile(gifDocument.getFileId());
            state.setGif(gifFile);
        } catch (IOException e) {
            log.error(e.getMessage());
            return finishCommand(state, chatId, ERROR);
        }

        return finishCommand(state, chatId, GIF_SAVED);
    }

    private State finishCommand(State state, Long chatId, String message) {
        telegramService.sendMessage(chatId, message);
        telegramService.sendMessage(chatId, messageFactory.zerGehitu(state));
        state.update(StateEnum.AWAIT_COMMAND);
        return state;
    }
}
