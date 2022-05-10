package ru.danis0n.telegrambot.DAO;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.danis0n.telegrambot.entity.Note;
import ru.danis0n.telegrambot.entity.User;
import ru.danis0n.telegrambot.repo.NoteRepository;
import ru.danis0n.telegrambot.repo.UserRepository;

import java.util.List;

@Service
public class NoteDAO {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    @Autowired
    public NoteDAO(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    public List<Note> findByUserId(long id) {
        User user = userRepository.findById(id);
        return user.getNotes();
    }

    public List<Note> findAllNotes() {
        return noteRepository.findAll();
    }

    public Note findByNoteId(long noteId){
        return noteRepository.findById(noteId);
    }

    public void remove(Note note) {
        noteRepository.delete(note);
    }

    public void save(Note note) {
        noteRepository.save(note);
    }

}
