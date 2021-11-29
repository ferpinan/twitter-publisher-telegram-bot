package com.github.ferpinan.twitterbot.command;

import com.github.ferpinan.twitterbot.dto.TelegramUpdate;
import com.github.ferpinan.twitterbot.factory.MessageFactory;
import com.github.ferpinan.twitterbot.service.TelegramService;
import com.github.ferpinan.twitterbot.state.State;
import com.github.ferpinan.twitterbot.state.StateEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.IOException;

import static com.github.ferpinan.twitterbot.command.GifReaderCommand.PHOTOS_ALREADY_UPLOADED;
import static com.github.ferpinan.twitterbot.command.GifReaderCommand.GIF_ALREADY_UPLOADED;
import static com.github.ferpinan.twitterbot.command.GifReaderCommand.GIF_NOT_RECEIVED;
import static com.github.ferpinan.twitterbot.command.GifReaderCommand.GIF_SAVED;
import static com.github.ferpinan.twitterbot.command.GifReaderCommand.VIDEO_ALREADY_UPLOADED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class GifReaderCommandTest {

    public static final long CHAT_ID = 12L;
    public static final String FILE_ID = "fileId";
    public static final String ZER_GEHITU_MSG = "zerGehitu";
    @InjectMocks
    private GifReaderCommand gifReaderCommand;

    @Mock
    private TelegramService telegramService;
    @Mock
    private MessageFactory messageFactory;

    @Mock
    private TelegramUpdate telegramUpdate;
    @Mock
    private Update update;
    @Mock
    private Message message;

    private State state;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        state = new State();
        when(telegramUpdate.getUpdate()).thenReturn(update);
        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(CHAT_ID);
        when(messageFactory.zerGehitu(any())).thenReturn(ZER_GEHITU_MSG);
    }

    @Test
    void shouldDownloadFileAndSendOKMessagesWhenGifIsReceivedCorrectly() throws IOException {
        Document document = mock(Document.class);
        when(message.getDocument()).thenReturn(document);
        when(document.getFileId()).thenReturn(FILE_ID);
        File fileMock = mock(File.class);
        when(telegramService.downloadFile(any())).thenReturn(fileMock);

        State result = gifReaderCommand.execute(telegramUpdate, state);

        verify(telegramService).downloadFile(FILE_ID);
        verify(telegramService).sendMessage(CHAT_ID, GIF_SAVED);
        verify(telegramService).sendMessage(CHAT_ID, ZER_GEHITU_MSG);
        verifyNoMoreInteractions(telegramService);
        verify(messageFactory).zerGehitu(state);
        verifyNoMoreInteractions(messageFactory);

        assertEquals(StateEnum.AWAIT_COMMAND, result.getCurrentState());
        assertNotNull(result.getGif());
        assertEquals(fileMock, result.getGif());
    }

    @Test
    void shouldNotDownloadFileAndSendErrorMessagesWhenGifIsNull() {
        when(message.getDocument()).thenReturn(null);

        State result = gifReaderCommand.execute(telegramUpdate, state);

        verify(telegramService).sendMessage(CHAT_ID, GIF_NOT_RECEIVED);
        verify(telegramService).sendMessage(CHAT_ID, ZER_GEHITU_MSG);
        verifyNoMoreInteractions(telegramService);
        verify(messageFactory).zerGehitu(state);
        verifyNoMoreInteractions(messageFactory);

        assertEquals(StateEnum.AWAIT_COMMAND, result.getCurrentState());
        assertNull(result.getGif());
    }

    @Test
    void shouldNotDownloadFileAndSendErrorMessagesWhenGifWasAlreadySet() {
        File fileMock = mock(File.class);
        state.setGif(fileMock);

        State result = gifReaderCommand.execute(telegramUpdate, state);

        verify(telegramService).sendMessage(CHAT_ID, GIF_ALREADY_UPLOADED);
        verify(telegramService).sendMessage(CHAT_ID, ZER_GEHITU_MSG);
        verifyNoMoreInteractions(telegramService);
        verify(messageFactory).zerGehitu(state);
        verifyNoMoreInteractions(messageFactory);

        assertEquals(StateEnum.AWAIT_COMMAND, result.getCurrentState());
        assertNotNull(result.getGif());
        assertEquals(fileMock, result.getGif());
    }

    @Test
    void shouldNotDownloadFileAndSendErrorMessagesWhenPhotosAreAlreadySet() {
        File fileMock = mock(File.class);
        state.addPhoto(fileMock);

        State result = gifReaderCommand.execute(telegramUpdate, state);

        verify(telegramService).sendMessage(CHAT_ID, PHOTOS_ALREADY_UPLOADED);
        verify(telegramService).sendMessage(CHAT_ID, ZER_GEHITU_MSG);
        verifyNoMoreInteractions(telegramService);
        verify(messageFactory).zerGehitu(state);
        verifyNoMoreInteractions(messageFactory);

        assertEquals(StateEnum.AWAIT_COMMAND, result.getCurrentState());
        assertNull(result.getGif());
    }

    @Test
    void shouldNotDownloadFileAndSendErrorMessagesWhenVideoIsAlreadySet() {
        File fileMock = mock(File.class);
        state.setVideo(fileMock);

        State result = gifReaderCommand.execute(telegramUpdate, state);

        verify(telegramService).sendMessage(CHAT_ID, VIDEO_ALREADY_UPLOADED);
        verify(telegramService).sendMessage(CHAT_ID, ZER_GEHITU_MSG);
        verify(messageFactory).zerGehitu(state);
        verifyNoMoreInteractions(telegramService, messageFactory);

        assertEquals(StateEnum.AWAIT_COMMAND, result.getCurrentState());
        assertNull(result.getGif());
    }
}