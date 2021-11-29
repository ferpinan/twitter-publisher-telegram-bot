package com.github.ferpinan.twitterbot.command;

import com.github.ferpinan.twitterbot.dto.TelegramUpdate;
import com.github.ferpinan.twitterbot.service.TelegramService;
import com.github.ferpinan.twitterbot.state.State;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import static com.github.ferpinan.twitterbot.state.StateEnum.AWAIT_VIDEO;
import static com.github.ferpinan.twitterbot.state.StateEnum.RECEIVED_VIDEO_COMMAND;
import static com.github.ferpinan.twitterbot.utils.Messages.CHOOSE_VIDEO_MSG;
import static com.github.ferpinan.twitterbot.utils.Messages.ERROR_MSG;

@Component
@RequiredArgsConstructor
@Log4j2
public class AwaitVideoCommand implements Command{

    private final TelegramService telegramService;

    @Override
    public State execute(TelegramUpdate telegramUpdate, State state) {
        Long chatId = telegramUpdate.getUpdate().getMessage().getChatId();

        if(state.isNot(RECEIVED_VIDEO_COMMAND)){
            log.info("{} state expected but it was {}", RECEIVED_VIDEO_COMMAND, state.getCurrentState());
            telegramService.sendMessage(chatId, ERROR_MSG);
            return state;
        }

        telegramService.sendMessage(chatId, CHOOSE_VIDEO_MSG);
        state.update(AWAIT_VIDEO);
        return state;
    }
}
