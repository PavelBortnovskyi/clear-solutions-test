package com.neo.service;

import com.neo.domain.Address;
import com.neo.domain.Phone;
import com.neo.domain.User;
import com.neo.dto.rq.UserDTOrq;
import com.neo.dto.rs.UserDTOrs;
import com.neo.exceptions.validation.AgeException;
import com.neo.exceptions.validation.UserNotFoundException;
import com.neo.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Spy
    private ModelMapper modelMapper;

    private UserDTOrq userDTOrqSample1;
    private UserDTOrq userDTOrqSample2;
    private User userSample1;
    private User userSample2;
    private UserDTOrs userDTOrsSample1;
    private UserDTOrs userDTOrsSample2;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(userService, "ageLimit", 18);

        modelMapper.getConfiguration().setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setPropertyCondition(u -> u.getSource() != null);

        userDTOrqSample1 = new UserDTOrq();
        userDTOrqSample1.setFirstName("John");
        userDTOrqSample1.setLastName("Doe");
        userDTOrqSample1.setBirthDate(LocalDate.of(1990, 1, 1));
        userDTOrqSample1.setEmail("john.doe@example.com");
        Address address1 = new Address();
        address1.setCity("Anycity");
        address1.setStreet("123 Main St");
        address1.setNumber("99");
        address1.setApt("01");
        address1.setZip("55444");
        userDTOrqSample1.setAddress(address1);
        Phone user1Phone1 = new Phone();
        user1Phone1.setNumber("000-000");
        user1Phone1.setType(Phone.Type.HOME);
        Phone user1Phone2 = new Phone();
        user1Phone2.setNumber("111-111");
        user1Phone2.setType(Phone.Type.MOBILE);
        userDTOrqSample1.setPhones(List.of(user1Phone1, user1Phone2));

        userSample1 = new User();
        userSample1.setId(1L);
        userSample1.setFirstName("John");
        userSample1.setLastName("Doe");
        userSample1.setBirthDate(LocalDate.of(1990, 1, 1));
        userSample1.setEmail("john.doe@example.com");
        userSample1.setAddress(address1);
        userSample1.addPhone(user1Phone1);
        userSample1.addPhone(user1Phone2);

        userDTOrsSample1 = new UserDTOrs();
        userDTOrsSample1.setFirstName("John");
        userDTOrsSample1.setLastName("Doe");
        userDTOrsSample1.setBirthDate(LocalDate.of(1990, 1, 1));
        userDTOrsSample1.setEmail("john.doe@example.com");
        userDTOrsSample1.setAddress(address1);
        userDTOrsSample1.setPhones(List.of(user1Phone1, user1Phone2));

        userDTOrqSample2 = new UserDTOrq();
        userDTOrqSample2.setFirstName("David");
        userDTOrqSample2.setLastName("Martinez");
        userDTOrqSample2.setBirthDate(LocalDate.of(2000, 5, 23));
        userDTOrqSample2.setEmail("david.doe@ukr.net");
        Address address2 = new Address();
        address2.setCity("NightCity");
        address2.setStreet("321 Secondary St");
        address2.setNumber("66");
        address2.setApt("02");
        address2.setZip("44555");
        userDTOrqSample2.setAddress(address2);
        Phone user2Phone1 = new Phone();
        user2Phone1.setNumber("222-222");
        user2Phone1.setType(Phone.Type.WORK);
        userDTOrqSample2.setPhones(List.of(user2Phone1));

        userSample2 = new User();
        userSample2.setId(2L);
        userSample2.setFirstName("David");
        userSample2.setLastName("Martinez");
        userSample2.setBirthDate(LocalDate.of(2000, 5, 23));
        userSample2.setEmail("david.doe@ukr.net");
        userSample2.setAddress(address2);
        userSample2.addPhone(user2Phone1);

        userDTOrsSample2 = new UserDTOrs();
        userDTOrsSample2.setFirstName("David");
        userDTOrsSample2.setLastName("Martinez");
        userDTOrsSample2.setBirthDate(LocalDate.of(2000, 5, 23));
        userDTOrsSample2.setEmail("david.doe@ukr.net");
        userDTOrsSample2.setAddress(address2);
        userDTOrsSample2.setPhones(List.of(user2Phone1));
    }

    @Test
    public void testCheckUserAgeThrowsException() {
        LocalDate age = LocalDate.now().minusDays(1);
        Assertions.assertThatThrownBy(() -> userService.checkUserAge(age))
                .isInstanceOf(AgeException.class)
                .hasMessageContaining("You are too young my friend! This service is only for 18+ people");
    }

    @Test
    public void testCreateUserReturnsUserDTOrs() {
        when(userRepository.save(any(User.class))).thenReturn(userSample1);

        UserDTOrs savedUserDTOrs = userService.createUser(userDTOrqSample1);

        Assertions.assertThat(savedUserDTOrs).isNotNull();
        Assertions.assertThat(savedUserDTOrs.equals(userDTOrsSample1)).isTrue();
        verify(userRepository, Mockito.times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUserReturnsUserDTOrs() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(userSample1));
        when(userRepository.save(any(User.class))).thenReturn(userSample2);

        UserDTOrs updatedUserDTOrs = userService.updateUser(1L, userDTOrqSample2);

        Assertions.assertThat(updatedUserDTOrs).isNotNull();
        Assertions.assertThat(updatedUserDTOrs.equals(userDTOrsSample2)).isTrue();
        verify(userRepository, Mockito.times(1)).save(any(User.class));
    }

//    @Test
//    public void testUpdateUserThrowsException() {
//        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
//        Assertions.assertThatThrownBy(() -> userService.updateUser(1L, userDTOrqSample1))
//                .isInstanceOf(UserNotFoundException.class)
//                .hasMessageContaining("User with id: 1 is not present");
//        verify(userRepository, Mockito.times(0)).save(any(User.class));
//    }

    @Test
    public void testRecursiveUpdateFields() {
        UserDTOrq patchDTOrq = new UserDTOrq();
        patchDTOrq.setFirstName("Kevin");
        Address patchAddress = new Address();
        patchAddress.setZip("9999");
        patchDTOrq.setAddress(patchAddress);
        Phone patchPhone = new Phone();
        patchPhone.setType(Phone.Type.WORK);
        patchPhone.setNumber("222-222");
        List<Phone> patchPhoneList = new ArrayList<>(userSample1.getPhones());
        patchPhoneList.add(patchPhone);
        patchDTOrq.setPhones(patchPhoneList);

        userService.recursiveUpdateFields(userSample1, patchDTOrq);

        Assertions.assertThat(userSample1).isNotNull();
        Assertions.assertThat(userSample1.getFirstName().equals("Kevin")).isTrue();
        Assertions.assertThat(userSample1.getAddress().getZip().equals("9999")).isTrue();
        Assertions.assertThat(userSample1.getPhones().size()).isEqualTo(3);
        Assertions.assertThat(userSample1.getPhones().contains(patchPhone)).isTrue();

    }

    @Test
    public void testPatchUserReturnsUserDTOrs() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(userSample1));

        UserDTOrq patchDTOrq = new UserDTOrq();
        patchDTOrq.setFirstName("Kevin");

        UserDTOrs patchedUserDTOrs = userService.patchUser(1L, patchDTOrq);

        Assertions.assertThat(patchedUserDTOrs).isNotNull();
        Assertions.assertThat(patchedUserDTOrs.getFirstName().equals("Kevin")).isTrue();
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(userRepository, Mockito.times(0)).save(any(User.class));
    }

    @Test
    public void testPatchUserThrowsException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> userService.patchUser(1L, userDTOrqSample2))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User with id: 1 is not present");
        verify(userRepository, Mockito.times(0)).save(any(User.class));
    }

    @Test
    public void testFindUsersByBirthDateInRangeThrowsException() {
        LocalDate from = LocalDate.of(1800, 01, 01);
        LocalDate to = LocalDate.of(2024, 01, 01);
        Assertions.assertThatThrownBy(() -> userService.findUsersByBirthDateInRange(to, from, Pageable.ofSize(1).withPage( 0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("From is after than to in range!");
    }
}
