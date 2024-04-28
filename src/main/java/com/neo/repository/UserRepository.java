package com.neo.repository;

import com.neo.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.phones WHERE u.birthDate BETWEEN :from AND :to" )
    Page<User> findByBirthDateBetween(@Param("from")LocalDate from, @Param("to") LocalDate to, Pageable pageable);
}
