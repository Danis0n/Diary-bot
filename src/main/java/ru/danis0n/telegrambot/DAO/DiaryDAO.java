package ru.danis0n.telegrambot.DAO;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.danis0n.telegrambot.entity.Diary;
import ru.danis0n.telegrambot.entity.User;
import ru.danis0n.telegrambot.repo.DiaryRepository;
import ru.danis0n.telegrambot.repo.UserRepository;

import java.util.List;

@Service
public class DiaryDAO {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    @Autowired
    public DiaryDAO(DiaryRepository diaryRepository, UserRepository userRepository) {
        this.diaryRepository = diaryRepository;
        this.userRepository = userRepository;
    }


    public List<Diary> findByUserId(long id) {
        User user = userRepository.findById(id);
        return user.getNotes();
    }

    public List<Diary> findAllDiaries() {
        return diaryRepository.findAll();
    }

    public Diary findByDiaryId(long diaryId){
        return diaryRepository.findById(diaryId);
    }

    public void remove(Diary diary) {
        diaryRepository.delete(diary);
    }

    public void save(Diary diary) {
        diaryRepository.save(diary);
    }

}
