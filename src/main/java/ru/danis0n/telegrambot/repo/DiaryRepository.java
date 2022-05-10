package ru.danis0n.telegrambot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.danis0n.telegrambot.entity.Diary;

public interface DiaryRepository extends JpaRepository<Diary,Long> {
    Diary findById(long id);
}
