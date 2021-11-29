package com.github.ferpinan.twitterbot.command;

import com.github.ferpinan.twitterbot.dto.TelegramUpdate;
import com.github.ferpinan.twitterbot.factory.MessageFactory;
import com.github.ferpinan.twitterbot.service.TelegramService;
import com.github.ferpinan.twitterbot.state.State;
import com.github.ferpinan.twitterbot.state.StateEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.github.ferpinan.twitterbot.state.StateEnum.AWAIT_COMMAND;

@Component
@RequiredArgsConstructor
@Log4j2
public class PhotoReaderCommand implements Command{

    protected static final String PHOTOS_SAVED = "Gorde da argazkia.";
    protected static final String PHOTOS_ALREADY_UPLOADED = "Dagoeneko igo duzu argazki hori.";
    protected static final String PHOTOS_NOT_RECEIVED = "Ez da argazkirik jaso.";
    protected static final String GIF_ALREADY_UPLOADED = "Dagoeneko igo duzu gif bat eta ezin da argazkirik txertatu";
    protected static final String VIDEO_ALREADY_UPLOADED = "Dagoeneko igo duzu bideo bat eta ezin da bideorik txertatu";
    protected static final String ERROR = "Error bat gertatu da argazkiak txertatzerakoan";

    private final TelegramService telegramService;
    private final MessageFactory messageFactory;

    @Override
    public State execute(TelegramUpdate telegramUpdate, State state) {
        Long chatId = telegramUpdate.getUpdate().getMessage().getChatId();

        if(Objects.nonNull(state.getGif())){
            return finishCommand(state, chatId, GIF_ALREADY_UPLOADED, telegramUpdate);
        }

        if(Objects.nonNull(state.getVideo())){
            return finishCommand(state, chatId, VIDEO_ALREADY_UPLOADED, telegramUpdate);
        }

        List<PhotoSize> photoSizeDocument = telegramUpdate.getUpdate().getMessage().getPhoto();
        Document photoDocument = telegramUpdate.getUpdate().getMessage().getDocument();

        if (isPhotoSizeDocumentEmpty(photoSizeDocument) && isPhotoDocumentEmpty(photoDocument)) {
            return finishCommand(state, chatId, PHOTOS_NOT_RECEIVED, telegramUpdate);
        }
        try {
            String fileId = null;
            String fileUniqueId = null;

            if(!isPhotoSizeDocumentEmpty(photoSizeDocument)) {
                fileId = photoSizeDocument.get(3).getFileId();
                log.debug("FileId: " + fileId);
                fileUniqueId = photoSizeDocument.get(3).getFileUniqueId();
            }else if(!isPhotoDocumentEmpty(photoDocument)){
                fileId = photoDocument.getFileId();
                log.debug("FileId: " + fileId);
                fileUniqueId = photoDocument.getFileUniqueId();
            }

            File photoFile = telegramService.downloadFile(fileId, fileUniqueId, "jpg");

            if(state.photoExists(photoFile)){
                return finishCommand(state, chatId, PHOTOS_ALREADY_UPLOADED, telegramUpdate);
            }
            state.addPhoto(photoFile);
        } catch (IOException e) {
            log.error(e.getMessage());
            return finishCommand(state, chatId, ERROR, telegramUpdate);
        }

        return finishCommand(state, chatId, PHOTOS_SAVED, telegramUpdate);
    }

    private boolean isPhotoDocumentEmpty(Document photoDocument) {
        return photoDocument ==null;
    }

    private boolean isPhotoSizeDocumentEmpty(List<PhotoSize> photoSizeDocument) {
        return photoSizeDocument == null || photoSizeDocument.isEmpty();
    }

    private State finishCommand(State state, Long chatId, String message, TelegramUpdate update) {
        telegramService.sendMessage(chatId, message);
        if(update.getIsLastUpdate()) {
            telegramService.sendMessage(chatId, messageFactory.zerGehitu(state));
            state.update(StateEnum.AWAIT_COMMAND);
        }
        return state;
    }
}
