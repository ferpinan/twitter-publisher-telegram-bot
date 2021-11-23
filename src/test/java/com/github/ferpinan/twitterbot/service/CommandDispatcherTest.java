package com.github.ferpinan.twitterbot.service;

import com.github.ferpinan.twitterbot.command.AwaitGifCommand;
import com.github.ferpinan.twitterbot.command.AwaitPhotosCommand;
import com.github.ferpinan.twitterbot.command.AwaitTextCommand;
import com.github.ferpinan.twitterbot.command.Command;
import com.github.ferpinan.twitterbot.command.FinishCommand;
import com.github.ferpinan.twitterbot.command.GifReaderCommand;
import com.github.ferpinan.twitterbot.command.MessageReaderCommand;
import com.github.ferpinan.twitterbot.command.NotStartedCommand;
import com.github.ferpinan.twitterbot.command.PasswordCheckerCommand;
import com.github.ferpinan.twitterbot.command.PhotoReaderCommand;
import com.github.ferpinan.twitterbot.command.StartCommand;
import com.github.ferpinan.twitterbot.dto.TelegramUpdate;
import com.github.ferpinan.twitterbot.state.State;
import com.github.ferpinan.twitterbot.state.StateEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class CommandDispatcherTest {

    private CommandDispatcher commandDispatcher;

    @Mock
    private StartCommand startCommand;
    @Mock
    private NotStartedCommand notStartedCommand;
    @Mock
    private PasswordCheckerCommand passwordCheckerCommand;
    @Mock
    private FinishCommand finishCommand;
    @Mock
    private MessageReaderCommand messageReaderCommand;
    @Mock
    private GifReaderCommand gifReaderCommand;
    @Mock
    private PhotoReaderCommand photoReaderCommand;
    @Mock
    private AwaitTextCommand awaitTextCommand;
    @Mock
    private AwaitGifCommand awaitGifCommand;
    @Mock
    private AwaitPhotosCommand awaitPhotosCommand;

    private Map<String, Command> commandsMap;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        commandDispatcher = spy(new CommandDispatcher(startCommand, notStartedCommand, passwordCheckerCommand, finishCommand, messageReaderCommand, gifReaderCommand, photoReaderCommand, awaitTextCommand, awaitGifCommand, awaitPhotosCommand));

        commandsMap = Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Command>("startCommand", startCommand),
                new AbstractMap.SimpleEntry<String, Command>("notStartedCommand", notStartedCommand),
                new AbstractMap.SimpleEntry<String, Command>("passwordCheckerCommand", passwordCheckerCommand),
                new AbstractMap.SimpleEntry<String, Command>("finishCommand", finishCommand),
                new AbstractMap.SimpleEntry<String, Command>("messageReaderCommand", messageReaderCommand),
                new AbstractMap.SimpleEntry<String, Command>("gifReaderCommand", gifReaderCommand),
                new AbstractMap.SimpleEntry<String, Command>("photoReaderCommand", photoReaderCommand),
                new AbstractMap.SimpleEntry<String, Command>("awaitTextCommand", awaitTextCommand),
                new AbstractMap.SimpleEntry<String, Command>("awaitGifCommand", awaitGifCommand),
                new AbstractMap.SimpleEntry<String, Command>("awaitPhotosCommand", awaitPhotosCommand)
        );
    }

    @ParameterizedTest
    @MethodSource("stringIntAndListProvider")
    void shouldCallAwaitTextCommandWhenCommandIsTextAndStateIsAwaitCommand(StateEnum stateEnum, String messageStr, String commandStr) {
        TelegramUpdate update = new TelegramUpdate();
        Message message = new Message();
        message.setFrom(new User());
        message.setText(messageStr);
        update.setMessage(message);

        State state = new State();
        state.setCurrentState(stateEnum);
        doReturn(state).when(commandDispatcher).initUserState(any());
        doNothing().when(commandDispatcher).saveUserState(any(), any());

        commandDispatcher.mainMethod(update);

        Command command = commandsMap.get(commandStr);
        if(command!=null) {
            verify(command).execute(update, state);
        }

        verifyNoMoreInteractions(startCommand, notStartedCommand, passwordCheckerCommand, finishCommand, messageReaderCommand, gifReaderCommand, photoReaderCommand, awaitTextCommand, awaitGifCommand, awaitPhotosCommand);
    }

    static Stream<Arguments> stringIntAndListProvider() {
        return Stream.of(
                arguments(StateEnum.NOT_STARTED, "/hasi", "startCommand"),
                arguments(StateEnum.NOT_STARTED, "asdf", "notStartedCommand"),
                arguments(StateEnum.NOT_STARTED, "/textua", "notStartedCommand"),
                arguments(StateEnum.NOT_STARTED, "/gif", "notStartedCommand"),
                arguments(StateEnum.NOT_STARTED, "/argazkiak", "notStartedCommand"),
                arguments(StateEnum.NOT_STARTED, "/bukatu", "notStartedCommand"),

                arguments(StateEnum.AWAIT_PASSWORD, "asdf", "passwordCheckerCommand"),
                arguments(StateEnum.AWAIT_PASSWORD, "/hasi", "passwordCheckerCommand"),
                arguments(StateEnum.AWAIT_PASSWORD, "/textua", "passwordCheckerCommand"),
                arguments(StateEnum.AWAIT_PASSWORD, "/gif", "passwordCheckerCommand"),
                arguments(StateEnum.AWAIT_PASSWORD, "/argazkiak", "passwordCheckerCommand"),
                arguments(StateEnum.AWAIT_PASSWORD, "/bukatu", "passwordCheckerCommand"),

                arguments(StateEnum.PASSWORD_KO, "asdf", "passwordCheckerCommand"),
                arguments(StateEnum.PASSWORD_KO, "/hasi", "passwordCheckerCommand"),
                arguments(StateEnum.PASSWORD_KO, "/textua", "passwordCheckerCommand"),
                arguments(StateEnum.PASSWORD_KO, "/gif", "passwordCheckerCommand"),
                arguments(StateEnum.PASSWORD_KO, "/argazkiak", "passwordCheckerCommand"),
                arguments(StateEnum.PASSWORD_KO, "/bukatu", "passwordCheckerCommand"),

                arguments(StateEnum.AWAIT_COMMAND, "asdf", ""),
                arguments(StateEnum.AWAIT_COMMAND, "/hasi", ""),
                arguments(StateEnum.AWAIT_COMMAND, "/textua", "awaitTextCommand"),
                arguments(StateEnum.AWAIT_COMMAND, "/gif", "awaitGifCommand"),
                arguments(StateEnum.AWAIT_COMMAND, "/argazkiak", "awaitPhotosCommand"),
                arguments(StateEnum.AWAIT_COMMAND, "/bukatu", "finishCommand"),

                arguments(StateEnum.AWAIT_TEXT, "asdf", "messageReaderCommand"),
                arguments(StateEnum.AWAIT_TEXT, "/hasi", "messageReaderCommand"),
                arguments(StateEnum.AWAIT_TEXT, "/textua", "messageReaderCommand"),
                arguments(StateEnum.AWAIT_TEXT, "/gif", "messageReaderCommand"),
                arguments(StateEnum.AWAIT_TEXT, "/argazkiak", "messageReaderCommand"),
                arguments(StateEnum.AWAIT_TEXT, "/bukatu", "messageReaderCommand"),

                arguments(StateEnum.AWAIT_GIF, "asdf", "gifReaderCommand"),
                arguments(StateEnum.AWAIT_GIF, "/hasi", "gifReaderCommand"),
                arguments(StateEnum.AWAIT_GIF, "/textua", "gifReaderCommand"),
                arguments(StateEnum.AWAIT_GIF, "/gif", "gifReaderCommand"),
                arguments(StateEnum.AWAIT_GIF, "/argazkiak", "gifReaderCommand"),
                arguments(StateEnum.AWAIT_GIF, "/bukatu", "gifReaderCommand"),

                arguments(StateEnum.AWAIT_PHOTOS, "asdf", "photoReaderCommand"),
                arguments(StateEnum.AWAIT_PHOTOS, "/hasi", "photoReaderCommand"),
                arguments(StateEnum.AWAIT_PHOTOS, "/textua", "photoReaderCommand"),
                arguments(StateEnum.AWAIT_PHOTOS, "/gif", "photoReaderCommand"),
                arguments(StateEnum.AWAIT_PHOTOS, "/argazkiak", "photoReaderCommand"),
                arguments(StateEnum.AWAIT_PHOTOS, "/bukatu", "photoReaderCommand")
        );
    }
}