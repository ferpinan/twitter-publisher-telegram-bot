package com.github.ferpinan.twitterbot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

@Configuration
@RequiredArgsConstructor
public class ExternalApiConfiguration {

    @Value("${twitter.consumerKey.public}")
    private String consumerKeyStr;
    @Value("${twitter.consumerKey.secret}")
    private String consumerSecretStr;
    @Value("${twitter.accessToken.public}")
    private String accessTokenStr;
    @Value("${twitter.accessToken.secret}")
    private String accessTokenSecretStr;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Bean
    public Twitter twitter() {
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(consumerKeyStr, consumerSecretStr);
        AccessToken accessToken = new AccessToken(accessTokenStr,
                accessTokenSecretStr);

        twitter.setOAuthAccessToken(accessToken);
        return twitter;
    }

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(botToken);
    }
}
