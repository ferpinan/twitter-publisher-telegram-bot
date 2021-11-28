package com.github.ferpinan.twitterbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TelegramUpdate {

    private Update update;
    private Boolean isLastUpdate;
}
