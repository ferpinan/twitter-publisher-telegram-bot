package com.github.ferpinan.twitterbot.command;

import com.github.ferpinan.twitterbot.dto.TelegramUpdate;
import com.github.ferpinan.twitterbot.factory.MessageFactory;
import com.github.ferpinan.twitterbot.service.TelegramService;
import com.github.ferpinan.twitterbot.state.State;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class PhotoReaderCommand implements Command{

    private final TelegramService telegramService;
    private final MessageFactory messageFactory;

    @Override
    public State execute(TelegramUpdate update, State state) {

        if (update.getMessage().getPhoto() == null || update.getMessage().getPhoto().isEmpty()) {
            // TODO tratamiento especial
            return state;
        }
        Long chatId = update.getMessage().getChatId();
        List<PhotoSize> photo = update.getMessage().getPhoto();
        try {
            log.debug("FileId: " + photo.get(3).getFileId());
            File file1 = telegramService.downloadFile(photo.get(3).getFileId(), photo.get(3).getFileUniqueId(), "jpg");
            state.addPhoto(file1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        telegramService.sendMessage(chatId, "Gorde da argazkia.");
        telegramService.sendMessage(chatId, messageFactory.zerGehitu(state));
        //state.update(StateEnum.AWAIT_COMMAND); doesn await command because it can receive more than one picture
        return state;
    }
}
