package com.github.ferpinan.twitterbot.service;

import com.github.ferpinan.twitterbot.command.AwaitGifCommand;
import com.github.ferpinan.twitterbot.command.AwaitPhotosCommand;
import com.github.ferpinan.twitterbot.command.AwaitTextCommand;
import com.github.ferpinan.twitterbot.command.GifReaderCommand;
import com.github.ferpinan.twitterbot.command.FinishCommand;
import com.github.ferpinan.twitterbot.command.MessageReaderCommand;
import com.github.ferpinan.twitterbot.command.NotStartedCommand;
import com.github.ferpinan.twitterbot.command.PasswordCheckerCommand;
import com.github.ferpinan.twitterbot.command.PhotoReaderCommand;
import com.github.ferpinan.twitterbot.command.StartCommand;
import com.github.ferpinan.twitterbot.dto.TelegramUpdate;
import com.github.ferpinan.twitterbot.state.State;
import com.github.ferpinan.twitterbot.state.StateEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CommandDispatcher {

    private final StartCommand startCommand;
    private final NotStartedCommand notStartedCommand;
    private final PasswordCheckerCommand passwordCheckerCommand;
    private final FinishCommand finishCommand;
    private final MessageReaderCommand messageReaderCommand;
    private final GifReaderCommand gifReaderCommand;
    private final PhotoReaderCommand photoReaderCommand;
    private final AwaitTextCommand awaitTextCommand;
    private final AwaitGifCommand awaitGifCommand;
    private final AwaitPhotosCommand awaitPhotosCommand;

    private Map<Long, State> stateMap;

    public void mainMethod(TelegramUpdate update) {
        // Esta función se invocará cuando nuestro bot reciba un mensaje

        Long userId = update.getMessage().getFrom().getId();

        State state = initState(userId);

        // Se obtiene el mensaje escrito por el usuario
        final String messageTextReceived = update.getMessage().getText();

        if (state.is(StateEnum.NOT_STARTED) && !"/hasi".equals(messageTextReceived)) {
            notStartedCommand.execute(update, state);
            return;
        }

        if ("/hasi".equals(messageTextReceived)) {
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
                gifReaderCommand.execute(update, state);
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
        }
    }

    private State initState(Long userId) {
        if(stateMap==null){
            stateMap = new HashMap<>();
        }
        return stateMap.computeIfAbsent(userId, k -> new State());
    }
}
