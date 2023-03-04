package ru.practicum.serv.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.serv.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByIdIn(List<Long> ids);

    Optional<User> findByName(String name);
}
