package com.neo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.domain.Address;
import com.neo.domain.Phone;
import com.neo.domain.User;
import com.neo.dto.rq.UserDTOrq;
import com.neo.dto.rs.UserDTOrs;
import com.neo.exceptions.validation.AgeException;
import com.neo.exceptions.validation.UserNotFoundException;
import com.neo.repository.UserRepository;
import com.neo.service.UserService;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    private UserDTOrq userDTOrqSample1;
    private UserDTOrq userDTOrqSample2;
    private User userSample1;
    private User userSample2;
    private UserDTOrs userDTOrsSample1;
    private UserDTOrs userDTOrsSample2;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(userService, "ageLimit", 18);

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
    @Order(1)
    public void testCreateUser() throws Exception {
        when(userService.createUser(Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample1);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTOrqSample1)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.firstName", CoreMatchers.is(userDTOrqSample1.getFirstName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lastName", CoreMatchers.is(userDTOrqSample1.getLastName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.birthDate", CoreMatchers.is(userDTOrqSample1.getBirthDate().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email", CoreMatchers.is(userDTOrqSample1.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.address.city", CoreMatchers.is(userDTOrqSample1.getAddress().getCity())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.address.street", CoreMatchers.is(userDTOrqSample1.getAddress().getStreet())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.address.number", CoreMatchers.is(userDTOrqSample1.getAddress().getNumber())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.address.apt", CoreMatchers.is(userDTOrqSample1.getAddress().getApt())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.address.zip", CoreMatchers.is(userDTOrqSample1.getAddress().getZip())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.phones", Matchers.hasSize(userDTOrqSample1.getPhones().size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.phones[*].number", Matchers.containsInAnyOrder(
                        userDTOrqSample1.getPhones().stream().map(Phone::getNumber).toArray()
                )))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.phones[*].type", Matchers.containsInAnyOrder(
                        userDTOrqSample1.getPhones().stream().map(phone -> phone.getType().toString()).toArray()
                )))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(2)
    public void testCreateUserWithEmptyFirstName() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample1;
        invalidSample.setFirstName(null);
        when(userService.createUser(Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample1);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName", CoreMatchers.is("firstName")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message", CoreMatchers.is("First name cannot be null! You have a name right?)")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(3)
    public void testCreateUserWithEmptyLastName() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample1;
        invalidSample.setLastName(null);
        when(userService.createUser(Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample1);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("lastName")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("Last name cannot be null! We are serious people here)")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(4)
    public void testCreateUserWithEmptyBirthDate() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample1;
        invalidSample.setBirthDate(null);
        when(userService.createUser(Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample1);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("birthDate")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("Sorry, but we need to know your age! How can we send you a gift without knowing this information?")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(5)
    public void testCreateUserWithEmptyEmail() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample1;
        invalidSample.setEmail(null);
        when(userService.createUser(Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample1);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("email")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("Sorry, but we need to know your email!")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(6)
    public void testCreateUserWithSingleCharFirstName() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample1;
        invalidSample.setFirstName("L");
        when(userService.createUser(Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample1);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("firstName")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("First name length must be in range 2..20 characters")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(7)
    public void testCreateUserWithTooLongFirstName() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample1;
        invalidSample.setFirstName("Alexandrinosphorus Magnifico III");
        when(userService.createUser(Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample1);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("firstName")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("First name length must be in range 2..20 characters")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(8)
    public void testCreateUserWithSingleCharLastName() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample1;
        invalidSample.setLastName("L");
        when(userService.createUser(Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample1);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("lastName")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("Last name length must be in range 2..20 characters")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(9)
    public void testCreateUserWithTooLongLastName() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample1;
        invalidSample.setLastName("Alexandrinosphorus Magnifico III");
        when(userService.createUser(Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample1);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("lastName")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("Last name length must be in range 2..20 characters")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(10)
    public void testCreateUserWithFutureBirthday() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample1;
        invalidSample.setBirthDate(LocalDate.now().plusDays(1));
        when(userService.createUser(Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample1);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("birthDate")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("Time traveler? At least world still exist in future. But use date earlier than present")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(11)
    public void testCreateUserTooYoung() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample1;
        invalidSample.setBirthDate(LocalDate.now().minusDays(17));
        when(userService.createUser(Mockito.any(UserDTOrq.class)))
                .thenThrow(new AgeException("You are too young my friend! This service is only for 18+ people"));

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("birthDate")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("You are too young my friend! This service is only for 18+ people")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(12)
    public void testCreateUserWithInvalidEmailFormat() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample1;
        invalidSample.setEmail("sdsgdbbfg.serjsgdf@ffdgf.khjkj");
        when(userService.createUser(Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample1);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("email")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("Invalid email format")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(13)
    public void testUpdateUser() throws Exception {
        when(userService.updateUser(Mockito.anyLong(), Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample2);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTOrqSample2)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.firstName", CoreMatchers.is(userDTOrqSample2.getFirstName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lastName", CoreMatchers.is(userDTOrqSample2.getLastName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.birthDate", CoreMatchers.is(userDTOrqSample2.getBirthDate().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email", CoreMatchers.is(userDTOrqSample2.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.address.city", CoreMatchers.is(userDTOrqSample2.getAddress().getCity())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.address.street", CoreMatchers.is(userDTOrqSample2.getAddress().getStreet())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.address.number", CoreMatchers.is(userDTOrqSample2.getAddress().getNumber())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.address.apt", CoreMatchers.is(userDTOrqSample2.getAddress().getApt())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.address.zip", CoreMatchers.is(userDTOrqSample2.getAddress().getZip())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.phones", Matchers.hasSize(userDTOrqSample2.getPhones().size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.phones[*].number", Matchers.containsInAnyOrder(
                        userDTOrqSample2.getPhones().stream().map(Phone::getNumber).toArray()
                )))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.phones[*].type", Matchers.containsInAnyOrder(
                        userDTOrqSample2.getPhones().stream().map(phone -> phone.getType().toString()).toArray()
                )))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(14)
    public void testUpdateUserThrowsException() throws Exception {
        when(userService.updateUser(Mockito.anyLong(), Mockito.any(UserDTOrq.class)))
                .thenThrow(new UserNotFoundException("User with id: 1 is not present"));

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTOrqSample2)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("userId")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("User with id: 1 is not present")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(15)
    public void testUpdateUserWithEmptyFirstName() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample2;
        invalidSample.setFirstName(null);
        when(userService.updateUser(Mockito.anyLong(), Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample2);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName", CoreMatchers.is("firstName")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message", CoreMatchers.is("First name cannot be null! You have a name right?)")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(16)
    public void testUpdateUserWithEmptyLastName() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample2;
        invalidSample.setLastName(null);
        when(userService.updateUser(Mockito.anyLong(), Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample2);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("lastName")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("Last name cannot be null! We are serious people here)")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(17)
    public void testUpdateUserWithEmptyBirthDate() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample2;
        invalidSample.setBirthDate(null);
        when(userService.updateUser(Mockito.anyLong(), Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample2);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("birthDate")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("Sorry, but we need to know your age! How can we send you a gift without knowing this information?")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(18)
    public void testUpdateUserWithEmptyEmail() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample2;
        invalidSample.setEmail(null);
        when(userService.updateUser(Mockito.anyLong(), Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample2);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("email")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("Sorry, but we need to know your email!")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(19)
    public void testUpdateUserWithSingleCharFirstName() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample2;
        invalidSample.setFirstName("L");
        when(userService.updateUser(Mockito.anyLong(), Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample2);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("firstName")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("First name length must be in range 2..20 characters")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(20)
    public void testUpdateUserWithTooLongFirstName() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample2;
        invalidSample.setFirstName("Alexandrinosphorus Magnifico III");
        when(userService.updateUser(Mockito.anyLong(), Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample2);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("firstName")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("First name length must be in range 2..20 characters")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(21)
    public void testUpdateUserWithSingleCharLastName() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample2;
        invalidSample.setLastName("L");
        when(userService.updateUser(Mockito.anyLong(), Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample2);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("lastName")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("Last name length must be in range 2..20 characters")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(22)
    public void testUpdateUserWithTooLongLastName() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample2;
        invalidSample.setLastName("Alexandrinosphorus Magnifico III");
        when(userService.updateUser(Mockito.anyLong(), Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample2);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("lastName")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("Last name length must be in range 2..20 characters")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(23)
    public void testUpdateUserWithFutureBirthday() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample2;
        invalidSample.setBirthDate(LocalDate.now().plusDays(1));
        when(userService.updateUser(Mockito.anyLong(), Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample2);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("birthDate")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("Time traveler? At least world still exist in future. But use date earlier than present")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(24)
    public void testUpdateUserTooYoung() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample2;
        invalidSample.setBirthDate(LocalDate.now().minusDays(17));
        when(userService.updateUser(Mockito.anyLong(), Mockito.any(UserDTOrq.class)))
                .thenThrow(new AgeException("You are too young my friend! This service is only for 18+ people"));

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("birthDate")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("You are too young my friend! This service is only for 18+ people")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(25)
    public void testUpdateUserWithInvalidEmailFormat() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample2;
        invalidSample.setEmail("sdsgdbbfg.serjsgdf@ffdgf.khjkj");
        when(userService.updateUser(Mockito.anyLong(), Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample2);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("email")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("Invalid email format")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(26)
    public void testPatchUser() throws Exception {
        UserDTOrq patchDTO = new UserDTOrq();
        patchDTO.setEmail("newMail@ukr.net");
        Address patchAddress = new Address();
        patchAddress.setApt("13");
        Phone patchPhone = new Phone();
        patchPhone.setNumber("999-999");
        patchPhone.setType(Phone.Type.HOME);
        patchDTO.setAddress(patchAddress);
        patchDTO.setPhones(List.of(patchPhone));

        userDTOrsSample1.setEmail(patchDTO.getEmail());
        userDTOrsSample1.getAddress().setApt(patchDTO.getAddress().getApt());
        userDTOrsSample1.setPhones(patchDTO.getPhones());

        when(userService.patchUser(Mockito.anyLong(), Mockito.any(UserDTOrq.class))).thenReturn(userDTOrsSample1);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchDTO)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.firstName", CoreMatchers.is(userDTOrqSample1.getFirstName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lastName", CoreMatchers.is(userDTOrqSample1.getLastName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.birthDate", CoreMatchers.is(userDTOrqSample1.getBirthDate().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email", CoreMatchers.is(patchDTO.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.address.city", CoreMatchers.is(userDTOrqSample1.getAddress().getCity())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.address.street", CoreMatchers.is(userDTOrqSample1.getAddress().getStreet())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.address.number", CoreMatchers.is(userDTOrqSample1.getAddress().getNumber())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.address.apt", CoreMatchers.is(patchDTO.getAddress().getApt())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.address.zip", CoreMatchers.is(userDTOrqSample1.getAddress().getZip())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.phones", Matchers.hasSize(patchDTO.getPhones().size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.phones[*].number", Matchers.containsInAnyOrder(
                        patchDTO.getPhones().stream().map(Phone::getNumber).toArray()
                )))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.phones[*].type", Matchers.containsInAnyOrder(
                        patchDTO.getPhones().stream().map(phone -> phone.getType().toString()).toArray()
                )))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(27)
    public void testPatchUserTooYoung() throws Exception {
        UserDTOrq invalidSample = userDTOrqSample2;
        invalidSample.setBirthDate(LocalDate.now().minusDays(17));
        when(userService.patchUser(Mockito.anyLong(), Mockito.any(UserDTOrq.class)))
                .thenThrow(new AgeException("You are too young my friend! This service is only for 18+ people"));

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSample)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("birthDate")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("You are too young my friend! This service is only for 18+ people")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(28)
    public void testDeleteUser() throws Exception {
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/1"));

        response.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @Order(29)
    public void testSearchUsersByBirthDateRangeNoPageAndSize() throws Exception {
        LocalDate fromDate = LocalDate.of(1990, 1, 1);
        LocalDate toDate = LocalDate.of(2000, 12, 31);

        List<UserDTOrs> expectedUsers = Arrays.asList(userDTOrsSample1, userDTOrsSample2);
        Page<UserDTOrs> expectedPage = new PageImpl<>(expectedUsers);

        when(userService.findUsersByBirthDateInRange(eq(fromDate), eq(toDate), eq(Pageable.ofSize(Integer.MAX_VALUE).withPage(0))))
                .thenReturn(expectedPage);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/search")
                .param("from", fromDate.toString())
                .param("to", toDate.toString()));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedPage)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements", CoreMatchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size", CoreMatchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.number", CoreMatchers.is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(30)
    public void testSearchUsersByBirthDateRangeWithPageAndSize() throws Exception {
        LocalDate fromDate = LocalDate.of(1990, 1, 1);
        LocalDate toDate = LocalDate.of(2000, 12, 31);

        Pageable pageable = PageRequest.of(1, 1);
        List<UserDTOrs> expectedUsers = Arrays.asList(userDTOrsSample1, userDTOrsSample2);
        Page<UserDTOrs> expectedPage = new PageImpl<>(expectedUsers, pageable, expectedUsers.size());

        when(userService.findUsersByBirthDateInRange(eq(fromDate), eq(toDate), eq(pageable)))
                .thenReturn(expectedPage);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/search")
                .param("from", fromDate.toString())
                .param("to", toDate.toString())
                .param("page", "1")
                .param("size", "1"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedPage)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements", CoreMatchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages", CoreMatchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.number", CoreMatchers.is(1)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(31)
    public void testSearchUsersByBirthDateRangeThrows() throws Exception {
        LocalDate fromDate = LocalDate.of(1990, 1, 1);
        LocalDate toDate = LocalDate.of(2000, 12, 31);

        Pageable pageable = PageRequest.of(0, 100);

        when(userService.findUsersByBirthDateInRange(eq(toDate), eq(fromDate), eq(pageable)))
                .thenThrow(new IllegalArgumentException("From is after than to in range!"));

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/search")
                .param("from", toDate.toString())
                .param("to", fromDate.toString())
                .param("page", "0")
                .param("size", "100"));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].fieldName",
                        CoreMatchers.is("fromDate")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
                        CoreMatchers.is("From is after than to in range!")))
                .andDo(MockMvcResultHandlers.print());
    }
}
