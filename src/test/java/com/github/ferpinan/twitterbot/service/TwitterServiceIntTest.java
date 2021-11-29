package com.github.ferpinan.twitterbot.service;

import com.github.ferpinan.twitterbot.state.State;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TwitterServiceIntTest {

    @Autowired
    private TwitterService twitterService;

    @Test
    void publishTextTweet() {
        State state = new State();
        String message = "Proba";
        state.setMessage(message);
        try {
            Status tweet = twitterService.tweet(state);
            long tweetId = tweet.getId();
            Status tweet1 = twitterService.getTweet(tweetId);
            assertEquals(message, tweet1.getText());

            twitterService.deleteTweet(tweetId);

            TwitterException exception = Assertions.assertThrows(TwitterException.class, () -> {
                twitterService.getTweet(tweetId);
            });

            Assertions.assertTrue(exception.getMessage().contains("No status found with that ID"));
        } catch (TwitterException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}