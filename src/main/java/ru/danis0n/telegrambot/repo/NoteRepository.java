package ru.danis0n.telegrambot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.danis0n.telegrambot.entity.Note;

public interface NoteRepository extends JpaRepository<Note,Long> {
    Note findById(long id);
}
