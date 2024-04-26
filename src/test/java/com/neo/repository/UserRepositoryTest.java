package com.neo.repository;


import com.neo.domain.Address;
import com.neo.domain.Phone;
import com.neo.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByBirthDateBetweenReturnsCorrectPage() {
        User userSample1 = new User();
        userSample1.setFirstName("John");
        userSample1.setLastName("Doe");
        userSample1.setBirthDate(LocalDate.of(1990, 1, 1));
        userSample1.setEmail("john.doe@example.com");
        Address address1 = new Address();
        address1.setCity("Anycity");
        address1.setStreet("123 Main St");
        address1.setNumber("99");
        address1.setApt("01");
        address1.setZip("55444");
        userSample1.setAddress(address1);
        Phone user1Phone1 = new Phone();
        user1Phone1.setNumber("000-000");
        user1Phone1.setType(Phone.Type.HOME);
        Phone user1Phone2 = new Phone();
        user1Phone2.setNumber("111-111");
        user1Phone2.setType(Phone.Type.MOBILE);
        userSample1.addPhone(user1Phone1);
        userSample1.addPhone(user1Phone2);

        User userSample2 = new User();
        userSample2.setFirstName("David");
        userSample2.setLastName("Morales");
        userSample2.setBirthDate(LocalDate.of(2000, 5, 23));
        userSample2.setEmail("david.doe@ukr.net");
        Address address2 = new Address();
        address2.setCity("NightCity");
        address2.setStreet("321 Secondary St");
        address2.setNumber("66");
        address2.setApt("02");
        address2.setZip("44555");
        userSample2.setAddress(address2);
        Phone user2Phone1 = new Phone();
        user2Phone1.setNumber("222-222");
        user2Phone1.setType(Phone.Type.WORK);
        userSample2.addPhone(user2Phone1);

        userRepository.save(userSample1);
        userRepository.save(userSample2);

        LocalDate from = LocalDate.of(1989, 01, 01);
        LocalDate to = LocalDate.of(2000,5,24);

        Page<User> result = userRepository.findByBirthDateBetween(from, to, Pageable.ofSize(2).withPage(0));

        Assertions.assertThat(result.getContent().contains(userSample1)).isTrue();
        Assertions.assertThat(result.getContent().contains(userSample2)).isTrue();
        Assertions.assertThat(result.getTotalPages() == 1).isTrue();
        Assertions.assertThat(result.getTotalElements() == 2).isTrue();

        result = userRepository.findByBirthDateBetween(from, to, Pageable.ofSize(1).withPage(0));

        Assertions.assertThat(result.getTotalPages() == 2).isTrue();
        Assertions.assertThat(result.getTotalElements() == 2).isTrue();

        from = LocalDate.of(1991, 01, 01);

        result = userRepository.findByBirthDateBetween(from, to, Pageable.ofSize(1).withPage(0));

        Assertions.assertThat(result.getContent().contains(userSample1)).isFalse();
        Assertions.assertThat(result.getContent().contains(userSample2)).isTrue();
        Assertions.assertThat(result.getTotalPages() == 1).isTrue();
        Assertions.assertThat(result.getTotalElements() == 1).isTrue();

        from = LocalDate.of(2000, 5, 24);
        to = LocalDate.of(2000, 5, 25);

        result = userRepository.findByBirthDateBetween(from, to, Pageable.ofSize(2).withPage(0));

        Assertions.assertThat(result.getContent()).isEmpty();
        Assertions.assertThat(result.getTotalPages() == 0).isTrue();
        Assertions.assertThat(result.getTotalElements() == 0).isTrue();
    }
}
