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
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.github.ferpinan.twitterbot.command.PhotoReaderCommand.GIF_ALREADY_UPLOADED;
import static com.github.ferpinan.twitterbot.command.PhotoReaderCommand.PHOTOS_NOT_RECEIVED;
import static com.github.ferpinan.twitterbot.command.PhotoReaderCommand.PHOTOS_SAVED;
import static com.github.ferpinan.twitterbot.command.PhotoReaderCommand.PHOTOS_ALREADY_UPLOADED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class PhotoReaderCommandTest {

    public static final long CHAT_ID = 12L;
    public static final String FILE_ID = "fileId";
    public static final String ZER_GEHITU_MSG = "zerGehitu";

    @InjectMocks
    private PhotoReaderCommand photoReaderCommand;

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
        when(telegramUpdate.getIsLastUpdate()).thenReturn(true);
        when(message.getChatId()).thenReturn(CHAT_ID);
        when(messageFactory.zerGehitu(any())).thenReturn(ZER_GEHITU_MSG);
    }

    @Test
    void shouldDownloadFileAndSendOKMessagesWhenPhotoIsReceivedCorrectly() throws IOException {
        PhotoSize photoSize = mock(PhotoSize.class);
        when(message.getPhoto()).thenReturn(List.of(photoSize, photoSize, photoSize, photoSize));
        when(photoSize.getFileId()).thenReturn(FILE_ID);
        when(photoSize.getFileUniqueId()).thenReturn(FILE_ID);
        File fileMock = mock(File.class);
        when(telegramService.downloadFile(any(), any(), any())).thenReturn(fileMock);

        State result = photoReaderCommand.execute(telegramUpdate, state);

        verify(telegramService).downloadFile(FILE_ID,FILE_ID, "jpg");
        verify(telegramService).sendMessage(CHAT_ID, PHOTOS_SAVED);
        verify(telegramService).sendMessage(CHAT_ID, ZER_GEHITU_MSG);
        verify(messageFactory).zerGehitu(state);
        verifyNoMoreInteractions(telegramService, messageFactory);

        assertEquals(StateEnum.AWAIT_COMMAND, result.getCurrentState());
        assertNotNull(result.getPhotos());
        assertEquals(1, result.getPhotos().size());
        assertEquals(fileMock, result.getPhotos().get(0));
    }

    @Test
    void shouldNotDownloadFileAndSendErrorMessagesWhenPhotoIsNull() {
        when(message.getPhoto()).thenReturn(null);

        State result = photoReaderCommand.execute(telegramUpdate, state);

        verify(telegramService).sendMessage(CHAT_ID, PHOTOS_NOT_RECEIVED);
        verify(telegramService).sendMessage(CHAT_ID, ZER_GEHITU_MSG);
        verify(messageFactory).zerGehitu(state);
        verifyNoMoreInteractions(telegramService, messageFactory);

        assertEquals(StateEnum.AWAIT_COMMAND, result.getCurrentState());
        assertTrue(result.getPhotos().isEmpty());
    }

    @Test
    void shouldDownloadFileAndSendErrorMessagesWhenPhotoWasAlreadySet() throws IOException {
        PhotoSize photoSize = mock(PhotoSize.class);
        when(message.getPhoto()).thenReturn(List.of(photoSize, photoSize, photoSize, photoSize));
        when(photoSize.getFileId()).thenReturn(FILE_ID);
        when(photoSize.getFileUniqueId()).thenReturn(FILE_ID);
        File fileMock = mock(File.class);
        when(telegramService.downloadFile(any(), any(), any())).thenReturn(fileMock);
        state.addPhoto(fileMock);
        when(fileMock.getName()).thenReturn(FILE_ID);

        State result = photoReaderCommand.execute(telegramUpdate, state);

        verify(telegramService).downloadFile(FILE_ID,FILE_ID, "jpg");
        verify(telegramService).sendMessage(CHAT_ID, PHOTOS_ALREADY_UPLOADED);
        verify(telegramService).sendMessage(CHAT_ID, ZER_GEHITU_MSG);
        verify(messageFactory).zerGehitu(state);
        verifyNoMoreInteractions(telegramService, messageFactory);

        assertEquals(StateEnum.AWAIT_COMMAND, result.getCurrentState());
        assertEquals(fileMock, result.getPhotos().get(0));
        assertEquals(1, result.getPhotos().size());
    }

    @Test
    void shouldNotDownloadFileAndSendErrorMessagesWhenGifIsAlreadySet() {
        PhotoSize photoSize = mock(PhotoSize.class);
        when(message.getPhoto()).thenReturn(List.of(photoSize, photoSize, photoSize, photoSize));
        when(photoSize.getFileId()).thenReturn(FILE_ID);
        when(photoSize.getFileUniqueId()).thenReturn(FILE_ID);

        File fileMock = mock(File.class);
        state.setGif(fileMock);

        State result = photoReaderCommand.execute(telegramUpdate, state);

        verify(telegramService).sendMessage(CHAT_ID, GIF_ALREADY_UPLOADED);
        verify(telegramService).sendMessage(CHAT_ID, ZER_GEHITU_MSG);
        verify(messageFactory).zerGehitu(state);
        verifyNoMoreInteractions(telegramService, messageFactory);

        assertEquals(StateEnum.AWAIT_COMMAND, result.getCurrentState());
        assertNotNull(result.getGif());
        assertTrue(result.getPhotos().isEmpty());
    }
}