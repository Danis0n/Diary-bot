package ru.danis0n.telegrambot.model;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.danis0n.telegrambot.cash.BotStateCash;
import ru.danis0n.telegrambot.model.handler.CallbackQueryHandler;
import ru.danis0n.telegrambot.model.handler.MessageHandler;

// TODO: Read about JSON property

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramFacade {

    final MessageHandler messageHandler;
    final CallbackQueryHandler callbackQueryHandler;
    final BotStateCash botStateCash;

    @Value("${telegrambot.adminId}")
    int adminId;

    public TelegramFacade(MessageHandler messageHandler, CallbackQueryHandler callbackQueryHandler, BotStateCash botStateCash) {
        this.messageHandler = messageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
        this.botStateCash = botStateCash;
    }


    public BotApiMethod<?> handleUpdate(Update update) {

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return callbackQueryHandler.processCallbackQuery(callbackQuery);
        } else {

            Message message = update.getMessage();
            if (message != null && message.hasText()) {
                return handleInputMessage(message);
            }
        }
        return null;
    }

    private SendMessage handleInputMessage(Message message) {
        BotState botState;
        String inputMsg = message.getText();

        switch (inputMsg){

            case"/start":
                botState = BotState.START;
                break;
            case"Мои записи":
                botState = BotState.MYNOTES;
                break;
            case"Создать запись":
                botState = BotState.CREATE;
                break;
            case"Все пользователи":
                if(message.getFrom().getId() == adminId) botState = BotState.ALLUSERS;
                else botState = BotState.START;
                break;
            case"Все записи":
                if(message.getFrom().getId() == adminId) botState = BotState.ALLNOTES;
                else botState = BotState.START;
                break;
            default:
                botState = botStateCash.getBotStateMap().get(message.getFrom().getId()) == null?
                        BotState.START: botStateCash.getBotStateMap().get(message.getFrom().getId());
        }
        return messageHandler.handle(message,botState);
    }

}