package com.github.ferpinan.twitterbot.command;

import com.github.ferpinan.twitterbot.dto.TelegramUpdate;
import com.github.ferpinan.twitterbot.service.TelegramService;
import com.github.ferpinan.twitterbot.state.State;
import com.github.ferpinan.twitterbot.state.StateEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.ferpinan.twitterbot.state.StateEnum.AWAIT_COMMAND;
import static com.github.ferpinan.twitterbot.state.StateEnum.AWAIT_VIDEO;
import static com.github.ferpinan.twitterbot.state.StateEnum.RECEIVED_VIDEO_COMMAND;
import static com.github.ferpinan.twitterbot.utils.Messages.CHOOSE_VIDEO_MSG;
import static com.github.ferpinan.twitterbot.utils.Messages.ERROR_MSG;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class AwaitVideoCommandTest {

    @InjectMocks
    private AwaitVideoCommand awaitVideoCommand;

    @Mock
    private TelegramService telegramService;

    private TelegramUpdate telegramUpdate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        telegramUpdate = mock(TelegramUpdate.class);
        Update update = mock(Update.class);
        when(telegramUpdate.getUpdate()).thenReturn(update);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
    }

    @Test
    void whenStateIsCorrectItShouldChangeToAwaitVideoState() {
        State state = new State();
        state.setCurrentState(RECEIVED_VIDEO_COMMAND);

        State result = awaitVideoCommand.execute(telegramUpdate, state);

        assertNotNull(result);
        assertEquals(AWAIT_VIDEO, result.getCurrentState());

        verify(telegramService).sendMessage(anyLong(), eq(CHOOSE_VIDEO_MSG));
        verifyNoMoreInteractions(telegramService);
    }

    @Test
    void whenStateIsNotCorrectItShouldNotChangeStateAndSendErrorMessage() {
        State state = new State();
        state.setCurrentState(AWAIT_COMMAND);

        State result = awaitVideoCommand.execute(telegramUpdate, state);

        assertNotNull(result);
        assertEquals(AWAIT_COMMAND, result.getCurrentState());

        verify(telegramService).sendMessage(anyLong(), eq(ERROR_MSG));
        verifyNoMoreInteractions(telegramService);
    }
}