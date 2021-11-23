package com.github.ferpinan.twitterbot.service;

import com.github.ferpinan.twitterbot.command.AwaitGifCommand;
import com.github.ferpinan.twitterbot.command.AwaitPhotosCommand;
import com.github.ferpinan.twitterbot.command.AwaitTextCommand;
import com.github.ferpinan.twitterbot.command.Command;
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

import java.util.AbstractMap;
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

    private Map<StateEnum, Command> commandsMap;

    public void mainMethod(TelegramUpdate update) {
        commandsMap = Map.ofEntries(
                new AbstractMap.SimpleEntry<StateEnum, Command>(StateEnum.STARTED, startCommand),
                new AbstractMap.SimpleEntry<StateEnum, Command>(StateEnum.NOT_STARTED, notStartedCommand),
                new AbstractMap.SimpleEntry<StateEnum, Command>(StateEnum.AWAIT_PASSWORD, passwordCheckerCommand),
                new AbstractMap.SimpleEntry<StateEnum, Command>(StateEnum.PASSWORD_KO, passwordCheckerCommand),
                new AbstractMap.SimpleEntry<StateEnum, Command>(StateEnum.RECEIVED_FINISHED, finishCommand),
                new AbstractMap.SimpleEntry<StateEnum, Command>(StateEnum.RECEIVED_TEXT_COMMAND, awaitTextCommand),
                new AbstractMap.SimpleEntry<StateEnum, Command>(StateEnum.RECEIVED_GIF_COMMAND, awaitGifCommand),
                new AbstractMap.SimpleEntry<StateEnum, Command>(StateEnum.RECEIVED_PHOTOS_COMMAND, awaitPhotosCommand),
                new AbstractMap.SimpleEntry<StateEnum, Command>(StateEnum.AWAIT_TEXT, messageReaderCommand),
                new AbstractMap.SimpleEntry<StateEnum, Command>(StateEnum.AWAIT_GIF, gifReaderCommand),
                new AbstractMap.SimpleEntry<StateEnum, Command>(StateEnum.AWAIT_PHOTOS, photoReaderCommand)
        );
        // Esta función se invocará cuando nuestro bot reciba un mensaje

        Long userId = update.getMessage().getFrom().getId();

        State state = initUserState(userId);

        // Se obtiene el mensaje escrito por el usuario
        final String messageTextReceived = update.getMessage().getText();

        if(messageTextReceived.startsWith("/")){
            if (state.is(StateEnum.NOT_STARTED) && !"/hasi".equals(messageTextReceived)) {
                state.setCurrentState(StateEnum.NOT_STARTED);
            }else if(state.is(StateEnum.NOT_STARTED) && "/hasi".equals(messageTextReceived)){
                state.setCurrentState(StateEnum.STARTED);
            }else if ("/textua".equals(messageTextReceived) && state.is(StateEnum.AWAIT_COMMAND)) {
                state.setCurrentState(StateEnum.RECEIVED_TEXT_COMMAND);
            }else if ("/gif".equals(messageTextReceived) && state.is(StateEnum.AWAIT_COMMAND)) {
                state.setCurrentState(StateEnum.RECEIVED_GIF_COMMAND);
            }else if ("/argazkiak".equals(messageTextReceived) && state.is(StateEnum.AWAIT_COMMAND)) {
                state.setCurrentState(StateEnum.RECEIVED_PHOTOS_COMMAND);
            }else if ("/bukatu".equals(messageTextReceived) && state.is(StateEnum.AWAIT_COMMAND)) {
                state.setCurrentState(StateEnum.RECEIVED_FINISHED);
            }
        }

        Command command = commandsMap.get(state.getCurrentState());
        if(command!=null) {
            State newState = command.execute(update, state);
            saveUserState(userId, newState);
        }

    }

    protected State initUserState(Long userId) {
        if(stateMap==null){
            stateMap = new HashMap<>();
        }
        return stateMap.computeIfAbsent(userId, k -> new State());
    }

    protected void saveUserState(Long userId, State newState) {
        stateMap.put(userId, newState);
    }
}
