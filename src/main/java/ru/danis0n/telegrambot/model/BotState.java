package ru.danis0n.telegrambot.model;

public enum BotState {
    START, // starts the session
    CREATE, // create a note
    MYNOTES, // show notes for user
    ENTERNOTE, // the bot will wait for note to be entered
    ENTERDATE, // the bot will wait for date to be entered automatically
    ENTERNUMBERNOTE, // the bot will wait for number of note to be entered
    ALLUSERS, // show all users
    ALLNOTES, // show all notes
    ENTERNUMBERFOREDIT,
    ENTERNUMBERUSER, // the bot will wait for number of user to be entered
    EDITNOTE, // the bot will wait for note to be edited
    EDITDATE // the bot will wait for date to be edited
}
