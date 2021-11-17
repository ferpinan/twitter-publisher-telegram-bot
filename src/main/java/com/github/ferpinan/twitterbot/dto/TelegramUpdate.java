package com.github.ferpinan.twitterbot.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.telegram.telegrambots.meta.api.objects.Update;

@EqualsAndHashCode(callSuper = true)
@Data
public class TelegramUpdate extends Update {

    private Boolean isLastUpdate;
}
