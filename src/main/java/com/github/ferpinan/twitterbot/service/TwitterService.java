package com.github.ferpinan.twitterbot.service;

import com.github.ferpinan.twitterbot.state.State;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import twitter4j.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class TwitterService {

    private final Twitter twitter;

    public void tweet(State state) throws FileNotFoundException, TwitterException {
        StatusUpdate statusUpdate;
        statusUpdate = initStatusUpdate(state);

        if (!state.getPhotos().isEmpty()) {
            long[] photoIdArray = uploadPhotos(state.getPhotos());
            statusUpdate.setMediaIds(photoIdArray);
        }else if (state.getGif() != null) {
            Long gifMediaId = uploadDocument(state.getGif());
            statusUpdate.setMediaIds(gifMediaId);
        }else if(state.getVideo()!=null){
            Long gifMediaId = uploadDocument(state.getVideo());
            statusUpdate.setMediaIds(gifMediaId);
        }

        twitter.updateStatus(statusUpdate);
    }

    private StatusUpdate initStatusUpdate(State state) {
        if (state.getMessage() == null) {
            return new StatusUpdate("");
        }
        return new StatusUpdate(state.getMessage());
    }

    private long[] uploadPhotos(List<File> photoList) throws TwitterException {

        long[] photoIdArray = new long[photoList.size()];
        for (int i = 0; i < photoList.size(); i++) {
            File file = photoList.get(i);
            UploadedMedia media;
            media = twitter.uploadMedia(file);
            photoIdArray[i] = media.getMediaId();
        }

        if (photoIdArray.length == 0) {
            return null;
        }
        return photoIdArray;
    }

    private Long uploadDocument(File file) throws TwitterException, FileNotFoundException {
        UploadedMedia media = twitter.uploadMediaChunked(file.getName(), new BufferedInputStream(new FileInputStream(file)));
        if (media == null) {
            return null;
        }
        return media.getMediaId();
    }
}
