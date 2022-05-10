package ru.danis0n.telegrambot.cash;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.danis0n.telegrambot.entity.Diary;

import java.util.HashMap;
import java.util.Map;

@Service
@Setter
@Getter
// used to save entered event data per session
public class NoteCash {

    private final Map<Long, Diary> eventMap = new HashMap<>();

    public void saveNoteCash(long userId, Diary diary) {
        eventMap.put(userId, diary);
    }
}