package ru.danis0n.telegrambot.cash;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.danis0n.telegrambot.model.BotState;

import java.util.HashMap;
import java.util.Map;

@Service
@Setter
@Getter
//Used to save state bot.
public class BotStateCash {
    private final Map<Long, BotState> botStateMap = new HashMap<>();

    public void saveBotState(long userId, BotState botState) {
        botStateMap.put(userId, botState);
    }
}
