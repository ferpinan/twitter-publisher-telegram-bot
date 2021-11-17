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

    protected static final String GORDE_DA_GIFA_MSG = "Gorde da gif-a.";
    protected static final String DAGOENEKO_IGO_DUZU_GIF_BAT = "Dagoeneko igo duzu gif bat.";
    protected static final String EZ_DA_GIFIK_JASO_MSG = "Ez da gifik jaso.";
    public static final String DAGOENEKO_IGO_DITUZU_ARGAZKIAK_ETA_EZIN_DA_GIF_IK_TXERTATU = "Dagoeneko igo dituzu argazkiak eta ezin da gif-ik txertatu";
    public static final String ERROR_BAT_GERTATU_DA_GIF_A_TXERTATZERAKOAN = "Error bat gertatu da gif-a txertatzerakoan";

    private final TelegramService telegramService;
    private final MessageFactory messageFactory;

    @Override
    public State execute(TelegramUpdate update, State state) {
        Long chatId = update.getMessage().getChatId();
        Document document = update.getMessage().getDocument();

        if(document==null || StringUtils.isEmpty(document.getFileId())){
            return finishCommand(state, chatId, EZ_DA_GIFIK_JASO_MSG);
        }
        if(Objects.nonNull(state.getGif())){
            return finishCommand(state, chatId, DAGOENEKO_IGO_DUZU_GIF_BAT);
        }
        if(Objects.nonNull(state.getPhotos()) && !state.getPhotos().isEmpty()){
            return finishCommand(state, chatId, DAGOENEKO_IGO_DITUZU_ARGAZKIAK_ETA_EZIN_DA_GIF_IK_TXERTATU);
        }

        try {
            File gif = telegramService.downloadFile(document.getFileId());
            state.setGif(gif);
        } catch (IOException e) {
            log.error(e.getMessage());
            return finishCommand(state, chatId, ERROR_BAT_GERTATU_DA_GIF_A_TXERTATZERAKOAN);
        }

        return finishCommand(state, chatId, GORDE_DA_GIFA_MSG);
    }

    private State finishCommand(State state, Long chatId, String message) {
        telegramService.sendMessage(chatId, message);
        telegramService.sendMessage(chatId, messageFactory.zerGehitu(state));
        state.update(StateEnum.AWAIT_COMMAND);
        return state;
    }
}
