package ru.danis0n.telegrambot.model.handler;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.danis0n.telegrambot.DAO.NoteDAO;
import ru.danis0n.telegrambot.DAO.UserDAO;
import ru.danis0n.telegrambot.cash.BotStateCash;
import ru.danis0n.telegrambot.cash.NoteCash;
import ru.danis0n.telegrambot.entity.Note;
import ru.danis0n.telegrambot.entity.User;
import ru.danis0n.telegrambot.model.BotState;
import ru.danis0n.telegrambot.service.MenuService;

import javax.persistence.EntityNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
// basic event bot logic
public class EventHandler {

    private final UserDAO userDAO;
    private final NoteDAO noteDAO;
    private final BotStateCash botStateCash;
    private final MenuService menuService;
    private final NoteCash noteCash;

    @Value("${telegrambot.adminId}")
    private int admin_id;

    public EventHandler(UserDAO userDAO, NoteDAO noteDAO, BotStateCash botStateCash, MenuService menuService, NoteCash noteCash) {
        this.userDAO = userDAO;
        this.noteDAO = noteDAO;
        this.botStateCash = botStateCash;
        this.menuService = menuService;
        this.noteCash = noteCash;
    }

    public SendMessage saveNewUser(Message message, long userId, SendMessage sendMessage){
        String userName = message.getFrom().getUserName();
        User user = new User();
        user.setId(userId);
        user.setName(userName);
        userDAO.save(user);
        sendMessage.setText("Добро пожаловать на огонёк к моему боту! =)");
        botStateCash.saveBotState(userId, BotState.START);
        return sendMessage;
    }

    // remove (admin only)
    public SendMessage removeUserHandler(Message message, long userId){
        SendMessage sendMessage = new SendMessage();

        User user;
        try {
            long i = Long.parseLong(message.getText());
            user = userDAO.findByUserId(i);
        }catch (NumberFormatException e){
            sendMessage.setText("Введенная строка не является числом, попробуйте снова!");
            return sendMessage;
        }

        if(user == null){
            sendMessage.setText("Введенное число отсутсвует в списке, попробуйте снова!");
            return sendMessage;
        }
        userDAO.removeUser(user);
        botStateCash.saveBotState(userId,BotState.START);

        sendMessage.setText("Удаление произошло успешно!");
        return sendMessage;
    }

    // gets all events (admin only)
    public SendMessage getAllNotes(long userId){
        List<Note> notes = noteDAO.findAllNotes();
        botStateCash.saveBotState(userId,BotState.START);
        return noteListBuilder(userId,notes);
    }

    public SendMessage getAllUsers(long userId){
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(String.valueOf(userId));
        StringBuilder stringBuilder = new StringBuilder();
        List<User> users = userDAO.findAllUsers();
        for(User u : users){
            stringBuilder.append(buildUser(u));
        }
        replyMessage.setText(String.valueOf(stringBuilder));
        replyMessage.setReplyMarkup(menuService.getInlineMessageButtonsAllUser());
        botStateCash.saveBotState(userId,BotState.START);
        return replyMessage;
    }


    // gets all events of user
    public SendMessage myNotes(long userId){
        List<Note> notes = noteDAO.findByUserId(userId);
        return noteListBuilder(userId,notes);
    }

    // builds the notes for user to show
    public SendMessage noteListBuilder(long userId, List<Note> list) {
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(String.valueOf(userId));
        StringBuilder builder = new StringBuilder();
        if(list.isEmpty()){
            replyMessage.setText("Записи отсутствуют");
            return replyMessage;
        }
        for (Note note : list){
            builder.append(buildNote(note));
        }
        replyMessage.setText(builder.toString());
        replyMessage.setReplyMarkup(menuService.getInlineMessageButtons());
        return replyMessage;
    }

    public StringBuilder buildUser(User user){
        StringBuilder builder = new StringBuilder();
        long userId = user.getId();
        String name = user.getName();
        builder.append(userId).append(". ").append(name).append("\n");
        return builder;
    }

    public SendMessage editDate(Message message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        long userId = message.getFrom().getId();
        Date date;
        try {
            date = parseDate(message.getText());
        } catch (ParseException e) {
            sendMessage.setText("Не удается распознать указанную дату и время, попробуйте еще раз");
            return sendMessage;
        }
        Note note = noteCash.getEventMap().get(userId);
        note.setDate(date);
        noteCash.saveNoteCash(userId, note);
        return editNote(message.getChatId(),userId);
    }

    public SendMessage editText(Message message){

        long userId = message.getFrom().getId();
        String text = message.getText();
        if(text.length() < 4 || text.length() > 200){
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(message.getChatId()));
            sendMessage.setText("Недопустимая длина символов. Необходимое количество символов: 4 - 200");
            return sendMessage;
        }
        Note note = noteCash.getEventMap().get(userId);
        note.setDescription(text);

        noteCash.saveNoteCash(userId,note);
        return editNote(message.getChatId(),userId);
    }

    public SendMessage editNote(Message message, long userId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));

        Note note;
        try {
            note = enterNumberNote(message.getText(),userId);
        } catch (NumberFormatException e){
            sendMessage.setText("Введенная строка не является числом, попробуйте снова!");
            return sendMessage;
        }
        if(note == null) {
            sendMessage.setText("Введенное число отсутсвует в списке, попробуйте снова!");
            return sendMessage;
        }

        noteCash.saveNoteCash(userId,note);

        StringBuilder builder = buildNote(note);
        sendMessage.setText(builder.toString());
        sendMessage.setReplyMarkup(menuService.getInlineMessageEdit());
        return sendMessage;
    }

    public SendMessage enterNote(Message message, long userId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        String note = message.getText();
        if(note.length() < 4 || note.length() > 200){
            sendMessage.setText("Недопустимая длина символов. Необходимое количество символов: 4 - 200");
            return sendMessage;
        }
        botStateCash.saveBotState(userId,BotState.ENTERDATE);
        Note diary = noteCash.getEventMap().get(userId);
        diary.setDescription(note);
        noteCash.saveNoteCash(userId,diary);
        sendMessage.setText("Дата будет введена в соответствии с датой вашего устройства");
        return sendMessage;
    }

    public SendMessage enterDate(Message message, long userId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        Date date;
        try {
            date = parseDate(message.getText());
        }catch (ParseException e){
            sendMessage.setText("Не удаётся распознать указанную дату и время, попробуйте еще раз");
            return  sendMessage;
        }
        Note note = noteCash.getEventMap().get(userId);
        note.setDate(date);

        noteCash.saveNoteCash(userId, note);
        sendMessage.setText("Дата успешно введена!");
        saveNote(userId,message.getChatId());
        return sendMessage;
    }

    public Note enterNumberNote(String message, long userId) throws NumberFormatException, NullPointerException, EntityNotFoundException {
        List<Note> list;
        if(userId == admin_id){
            list = noteDAO.findAllNotes();
        }
        else{
            list = noteDAO.findByUserId(userId);
        }
        int i = Integer.parseInt(message);
        return list.stream().filter(diary -> diary.getEventId() == i).findFirst().orElseThrow(null);
    }

    public SendMessage removeNoteHandler(Message message,long userId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(userId));

        Note note;
        try {
            note = enterNumberNote(message.getText(),userId);
        } catch (NumberFormatException e){
            sendMessage.setText("Введенная строка не является числом, попробуйте снова!");
            return sendMessage;
        }
        if(note == null){
            sendMessage.setText("Введенное число отсутсвует в списке, попробуйте снова!");
            return sendMessage;
        }

        noteDAO.remove(note);
        botStateCash.saveBotState(userId,BotState.START);
        sendMessage.setText("Удаление прошло успешно");
        return sendMessage;
    }

    public SendMessage editNote(Long chatId, long userId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        Note note = noteCash.getEventMap().get(userId);
        if(note.getEventId() == 0){
            sendMessage.setText("Не удалось сохранить пользователя, нарушена последовательность действий");
            return sendMessage;
        }
        noteDAO.save(note);
        sendMessage.setText("Изменение сохранено");
        noteCash.saveNoteCash(userId,new Note());
        return sendMessage;
    }

    // builds the note for user to show
    private StringBuilder buildNote(Note note) {
        StringBuilder stringBuilder = new StringBuilder();
        long noteId = note.getEventId();
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date noteDate = note.getDate();
        String dateFormat = date.format(noteDate);

        String text = note.getDescription();
        stringBuilder.append(noteId).append(". ").append(dateFormat).append(": ")
                .append(text).append("\n");
        return stringBuilder;
    }

    public SendMessage saveNote(long userId, long chatId){
        Note note = noteCash.getEventMap().get(userId);
        note.setUser(userDAO.findByUserId(userId));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        noteDAO.save(note);
        noteCash.saveNoteCash(userId,new Note());
        sendMessage.setText("Запись успешно сохранена!");
        botStateCash.saveBotState(userId,BotState.START);
        return sendMessage;
    }

    private Date parseDate(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return simpleDateFormat.parse(s);
    }


}
