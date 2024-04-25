package com.neo.repository;

import com.neo.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findByBirthDateBetween(LocalDate from, LocalDate to, Pageable pageable);
}
