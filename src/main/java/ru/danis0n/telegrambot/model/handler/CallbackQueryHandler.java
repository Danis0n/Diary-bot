package ru.danis0n.telegrambot.model.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.danis0n.telegrambot.cash.BotStateCash;
import ru.danis0n.telegrambot.cash.NoteCash;
import ru.danis0n.telegrambot.model.BotState;
import ru.danis0n.telegrambot.service.MenuService;

@Component
public class CallbackQueryHandler {

    private final BotStateCash botStateCash;
    private final NoteCash noteCash;
    private final MenuService menuService;
    private final EventHandler eventHandler;

    @Autowired
    public CallbackQueryHandler(BotStateCash botStateCash, NoteCash noteCash, MenuService menuService, EventHandler eventHandler) {
        this.botStateCash = botStateCash;
        this.noteCash = noteCash;
        this.menuService = menuService;
        this.eventHandler = eventHandler;
    }

    public SendMessage processCallbackQuery(CallbackQuery callbackQuery){
        final long chatId = callbackQuery.getMessage().getChatId();
        final long userId = callbackQuery.getFrom().getId();

        SendMessage callBackAnswer = new SendMessage();
        callBackAnswer.setChatId(String.valueOf(chatId));

        String data = callbackQuery.getData();

        switch (data){
                // edit note
            case("buttonEdit"):
                callBackAnswer.setText("Введите номер записи из списка");
                botStateCash.saveBotState(userId, BotState.ENTERNUMBERFOREDIT);
                return callBackAnswer;
                // delete note
            case("buttonDelete"):
                callBackAnswer.setText("Введите номер записи из списка");
                botStateCash.saveBotState(userId,BotState.ENTERNUMBERNOTE);
                return callBackAnswer;
                // delete user (admin only)
            case("buttonDeleteUser"):
                callBackAnswer.setText("Введите номер пользователя");
                botStateCash.saveBotState(userId,BotState.ENTERNUMBERUSER);
                return callBackAnswer;
                //
            case("buttonDate"):
                if(noteCash.getEventMap().get(userId).getEventId() != 0){
                    callBackAnswer.setText("Введите дату");
                    botStateCash.saveBotState(userId, BotState.EDITDATE);
                }
                else {
                    callBackAnswer.setText("Нарушена последовательность действий");
                }
                return callBackAnswer;
            case("buttonDescription"):
                if(noteCash.getEventMap().get(userId).getEventId() != 0){
                    callBackAnswer.setText("Введите текст");
                    botStateCash.saveBotState(userId,BotState.EDITNOTE);
                }
                else {
                    callBackAnswer.setText("Нарушена последовательность действий");
                }
                return callBackAnswer;
            default:
                callBackAnswer.setText("Произошла ошибочка!\n");
        }
        return callBackAnswer;
    }

}
