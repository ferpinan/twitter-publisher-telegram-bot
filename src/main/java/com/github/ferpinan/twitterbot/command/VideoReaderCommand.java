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
import org.telegram.telegrambots.meta.api.objects.Video;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Log4j2
public class VideoReaderCommand implements Command{

    protected static final String VIDEO_SAVED = "Gorde da bideoa.";
    protected static final String VIDEO_ALREADY_UPLOADED = "Dagoeneko igo duzu bideo bat.";
    protected static final String VIDEO_NOT_RECEIVED = "Ez da bideorikik jaso.";
    protected static final String PHOTOS_ALREADY_UPLOADED = "Dagoeneko igo dituzu argazkiak eta ezin da bideorikk txertatu";
    protected static final String ERROR = "Error bat gertatu da bideoa txertatzerakoan";

    private final TelegramService telegramService;
    private final MessageFactory messageFactory;

    @Override
    public State execute(TelegramUpdate telegramUpdate, State state) {
        Long chatId = telegramUpdate.getUpdate().getMessage().getChatId();

        if(Objects.nonNull(state.getPhotos()) && !state.getPhotos().isEmpty()){
            return finishCommand(state, chatId, PHOTOS_ALREADY_UPLOADED);
        }
        if(Objects.nonNull(state.getVideo())){
            return finishCommand(state, chatId, VIDEO_ALREADY_UPLOADED);
        }

        Video videoDocument = telegramUpdate.getUpdate().getMessage().getVideo();

        if(videoDocument==null || StringUtils.isEmpty(videoDocument.getFileId())){
            return finishCommand(state, chatId, VIDEO_NOT_RECEIVED);
        }

        try {
            File videoFile = telegramService.downloadFile(videoDocument.getFileId());
            state.setVideo(videoFile);
        } catch (IOException e) {
            log.error(e.getMessage());
            return finishCommand(state, chatId, ERROR);
        }

        return finishCommand(state, chatId, VIDEO_SAVED);
    }

    private State finishCommand(State state, Long chatId, String message) {
        telegramService.sendMessage(chatId, message);
        telegramService.sendMessage(chatId, messageFactory.zerGehitu(state));
        state.update(StateEnum.AWAIT_COMMAND);
        return state;
    }
}
