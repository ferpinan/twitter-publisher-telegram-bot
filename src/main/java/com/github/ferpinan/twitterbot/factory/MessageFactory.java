package com.github.ferpinan.twitterbot.factory;

import com.github.ferpinan.twitterbot.state.State;
import org.springframework.stereotype.Component;

import static com.github.ferpinan.twitterbot.utils.StateUtils.isGifAttached;
import static com.github.ferpinan.twitterbot.utils.StateUtils.isPhotosAttached;

@Component
public class MessageFactory {
    public String zerGehitu(State state) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Zer gehitu nahi diozu txioari? \n");
        if (state.getMessage() == null) {
            stringBuilder.append("/textua\n");
        }
        if (!isGifAttached(state) && !isPhotosAttached(state)) {
            stringBuilder.append("/gif\n");
        }
        if (!isPhotosAttached(state) && !isGifAttached(state)) {
            stringBuilder.append("/argazkiak\n");
        }
        if (state.getMessage() != null
                || isGifAttached(state)
                || isPhotosAttached(state)) {
            stringBuilder.append("/bukatu\n");
        }
        return stringBuilder.toString();
    }
}
