package com.github.ferpinan.twitterbot.service;

import com.github.ferpinan.twitterbot.command.*;
import com.github.ferpinan.twitterbot.state.State;
import com.github.ferpinan.twitterbot.state.StateEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CommandDispatcher {

    private final StartCommand startCommand;
    private final NotStartedCommand notStartedCommand;
    private final PasswordCheckerCommand passwordCheckerCommand;
    private final FinishCommand finishCommand;
    private final MessageReaderCommand messageReaderCommand;
    private final DocumentReaderCommand documentReaderCommand;
    private final PhotoReaderCommand photoReaderCommand;
    private final AwaitTextCommand awaitTextCommand;
    private final AwaitGifCommand awaitGifCommand;
    private final AwaitPhotosCommand awaitPhotosCommand;

    private Map<Long, State> stateMap;

    public void mainMethod(Update update) {
        // Esta función se invocará cuando nuestro bot reciba un mensaje

        if(stateMap==null){
            stateMap = new HashMap<>();
        }

        Long userId = update.getMessage().getFrom().getId();
        State state = stateMap.get(userId);
        // Se obtiene el mensaje escrito por el usuario
        final String messageTextReceived = update.getMessage().getText();
        List<PhotoSize> photo = update.getMessage().getPhoto();
        System.out.println(photo);

        if (Objects.isNull(state) && !"/hasi".equals(messageTextReceived)) {
            notStartedCommand.execute(update, state);
            return;
        }

        if ("/hasi".equals(messageTextReceived)) {
            state = new State();
            stateMap.put(userId, state);
            startCommand.execute(update, state);
            return;
        }

        if (state.is(StateEnum.STARTED) || state.is(StateEnum.PASSWORD_KO)) {
            passwordCheckerCommand.execute(update, state);
            return;
        }

        if ("/textua".equals(messageTextReceived) && state.is(StateEnum.AWAIT_COMMAND)) {
            awaitTextCommand.execute(update, state);
            return;
        }

        if (state.is(StateEnum.AWAIT_TEXT)) {
            messageReaderCommand.execute(update, state);
            return;
        }

        if ("/gif".equals(messageTextReceived) && state.is(StateEnum.AWAIT_COMMAND)) {
            awaitGifCommand.execute(update, state);
            return;
        }

        if (state.is(StateEnum.AWAIT_GIF)) {
            if (update.getMessage().getDocument() != null) {
                documentReaderCommand.execute(update, state);
            }
            return;
        }

        if ("/argazkiak".equals(messageTextReceived) && state.is(StateEnum.AWAIT_COMMAND)) {
            awaitPhotosCommand.execute(update, state);
            return;
        }

        if ("/bukatu".equals(messageTextReceived)) {
            finishCommand.execute(update, state);
        }

        if (state.is(StateEnum.AWAIT_PHOTOS)) {
            if (update.getMessage().getPhoto() != null && !update.getMessage().getPhoto().isEmpty()) {
                photoReaderCommand.execute(update, state);
            }
            return;
        }
    }
}
