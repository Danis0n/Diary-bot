package ru.danis0n.telegrambot.model.handler;


import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.danis0n.telegrambot.DAO.UserDAO;
import ru.danis0n.telegrambot.cash.BotStateCash;
import ru.danis0n.telegrambot.cash.NoteCash;
import ru.danis0n.telegrambot.entity.Note;
import ru.danis0n.telegrambot.model.BotState;
import ru.danis0n.telegrambot.service.MenuService;

@Component
// process incoming text messages
public class MessageHandler {

    private final UserDAO userDAO;
    private final MenuService menuService;
    private final EventHandler eventHandler;
    private final BotStateCash botStateCash;
    private final NoteCash noteCash;

    public MessageHandler(UserDAO userDAO, MenuService menuService, EventHandler eventHandler, BotStateCash botStateCash, NoteCash noteCash) {
        this.userDAO = userDAO;
        this.menuService = menuService;
        this.eventHandler = eventHandler;
        this.botStateCash = botStateCash;
        this.noteCash = noteCash;
    }

    public SendMessage handle(Message message, BotState botState){
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        // if new user
        if(!userDAO.isExist(userId)){
            return eventHandler.saveNewUser(message,userId,sendMessage);
        }
        // save state in to cache
        botStateCash.saveBotState(userId,botState);

        switch (botState.name()){
            case("START"):
                return menuService.getMainMenuMessage(chatId,"Воспользуйтесь главным меню",userId);
            case("CREATE"):
                // starts create note
                botStateCash.saveBotState(userId,BotState.ENTERNOTE);
                noteCash.saveNoteCash(userId,new Note());
                sendMessage.setText("Введите запись");
                return sendMessage;
            case("ENTERNOTE"):
                return eventHandler.enterNote(message,userId);
            case("ENTERDATE"):
                return eventHandler.enterDate(message,userId);
            case("MYNOTES"):
                return eventHandler.myNotes(userId);
                // admin
            case("ALLNOTES"):
                return eventHandler.getAllNotes(userId);
                // admin
            case("ALLUSERS"):
                return eventHandler.getAllUsers(userId);
            case("EDITNOTE"):
                return eventHandler.editText(message);
            case("EDITDATE"):
                return eventHandler.editDate(message);
            case("ENTERNUMBERFOREDIT"):
                return eventHandler.editNote(message,userId);
            case("ENTERNUMBERUSER"):
                return eventHandler.removeUserHandler(message,userId);
            case("ENTERNUMBERNOTE"):
                return eventHandler.removeNoteHandler(message,userId);
            default:
                throw new IllegalStateException("Unexpected value: " + botState);
        }
    }
}
