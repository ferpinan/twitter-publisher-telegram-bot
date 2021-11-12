package com.github.ferpinan.twitterbot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

@Component
@RequiredArgsConstructor
public class TelegramService {

    private final TelegramBot telegramBot;

    public void sendMessage(Long chatId, String message) {
        sendMessage(chatId.toString(), message);
    }
    public void sendMessage(String chatId, String message) {
        telegramBot.execute(new SendMessage(chatId, message));
    }

    public java.io.File downloadFile(String fileId) throws IOException {
        return downloadFile(fileId, "gif", "mp4");
    }

    public java.io.File downloadFile(String fileId, String fileName, String extension) throws IOException {
        GetFile request = new GetFile(fileId);
        GetFileResponse getFileResponse = telegramBot.execute(request);

        File file = getFileResponse.file(); // com.pengrad.telegrambot.model.File
        file.fileId();
        file.filePath();  // relative path
        file.fileSize();
        String fullPath = telegramBot.getFullFilePath(file);

        URL url = new URL(fullPath);
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());

        java.io.File fileNew = new java.io.File(fileName + "." + extension);
        FileOutputStream fileOutputStream = new FileOutputStream(fileNew);
//        FileChannel fileChannel = fileOutputStream.getChannel();

        fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

        return fileNew;

    }

}
