package ru.danis0n.telegrambot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.danis0n.telegrambot.entity.User;

public interface UserRepository extends JpaRepository<User,Long> {
    User findById(long id);
}
