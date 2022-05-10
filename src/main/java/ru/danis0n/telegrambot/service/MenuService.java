package ru.danis0n.telegrambot.service;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.danis0n.telegrambot.DAO.UserDAO;

import java.util.ArrayList;
import java.util.List;

@Service
@Getter
@Setter
public class MenuService {

    private UserDAO userDAO;

    @Value("${telegrambot.adminId}")
    private int adminId;

    public MenuService(UserDAO userDAO){
        this.userDAO = userDAO;
    }

    private boolean isAdmin(long userId){
        return adminId == userId;
    }

    // sends main menu message to user
    public SendMessage getMainMenuMessage(final long chatId, final String textMessage, final long userId){
        final ReplyKeyboardMarkup replyKeyboardMarkup = getMainMenuKeyboard(userId);
        return createMessageWithKeyboard(chatId, textMessage, replyKeyboardMarkup);
    }

    private ReplyKeyboardMarkup getMainMenuKeyboard(long userId) {
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
//        User user = userDAO.findByUserId(userId);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow create = new KeyboardRow();
        KeyboardRow notes = new KeyboardRow();

        create.add(new KeyboardButton("Создать запись"));
        notes.add(new KeyboardButton("Мои записи"));
        keyboard.add(create);
        keyboard.add(notes);

        // admin panel
        if(isAdmin(userId)){
            KeyboardRow adminPanel = new KeyboardRow();
            adminPanel.add(new KeyboardButton("Все записи"));
            adminPanel.add(new KeyboardButton("Все пользователи"));
            keyboard.add(adminPanel);
        }
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    // sends the text message
    private SendMessage createMessageWithKeyboard(final long charId,
                                                  String textMessage,
                                                  final ReplyKeyboardMarkup replyKeyboardMarkup) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(charId));
        sendMessage.setText(textMessage);
        if(replyKeyboardMarkup != null){
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
            return sendMessage;
        }
        return sendMessage;
    }

    //set callbackquerry keyboard for list of events
    public InlineKeyboardMarkup getInlineMessageButtons(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonDelete = new InlineKeyboardButton();
        buttonDelete.setText("Удалить");
        buttonDelete.setCallbackData("buttonDelete");
        InlineKeyboardButton buttonEdit = new InlineKeyboardButton();
        buttonEdit.setText("Редактировать");
        buttonEdit.setCallbackData("buttonEdit");

        List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();
        keyboardButtons.add(buttonDelete);
        keyboardButtons.add(buttonEdit);

        List<List<InlineKeyboardButton>> row = new ArrayList<>();
        row.add(keyboardButtons);

        inlineKeyboardMarkup.setKeyboard(row);
        return inlineKeyboardMarkup;
    }

    //set callbackquerry for push edit
    public InlineKeyboardMarkup getInlineMessageEdit(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonDate = new InlineKeyboardButton();
        buttonDate.setText("Изменить дату");
        InlineKeyboardButton buttonDescription = new InlineKeyboardButton();
        buttonDescription.setText("Изменить описание");

        buttonDate.setCallbackData("buttonDate");
        buttonDescription.setCallbackData("buttonDescription");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();

        keyboardButtonsRow1.add(buttonDate);
        keyboardButtonsRow2.add(buttonDescription);

        List<List<InlineKeyboardButton>> row = new ArrayList<>();
        row.add(keyboardButtonsRow1);
        row.add(keyboardButtonsRow2);

        inlineKeyboardMarkup.setKeyboard(row);

        return inlineKeyboardMarkup;
    }

    //set calbackquery keyboard for users list
    public ReplyKeyboard getInlineMessageButtonsAllUser() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonDeleteUser = new InlineKeyboardButton();
        buttonDeleteUser.setText("Delete user");
        buttonDeleteUser.setCallbackData("buttonDeleteUser");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonDeleteUser);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

}
