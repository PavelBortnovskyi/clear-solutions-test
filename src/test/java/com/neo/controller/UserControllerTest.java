package com.neo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.domain.Address;
import com.neo.domain.Phone;
import com.neo.domain.User;
import com.neo.dto.rq.UserDTOrq;
import com.neo.repository.UserRepository;
import com.neo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private UserDTOrq userDTOrq1;

    private UserDTOrq userDTOrq2;

    @BeforeEach
    public void init() {
        userDTOrq1 = new UserDTOrq();
        userDTOrq1.setFirstName("John");
        userDTOrq1.setLastName("Doe");
        userDTOrq1.setBirthDate(LocalDate.of(1990, 1, 1));
        userDTOrq1.setEmail("john.doe@example.com");
        Address address1 = new Address();
        address1.setCity("Anycity");
        address1.setStreet("123 Main St");
        address1.setNumber("99");
        address1.setApt("01");
        address1.setZip("55444");
        userDTOrq1.setAddress(address1);
        Phone user1Phone1 = new Phone();
        user1Phone1.setNumber("000-000");
        user1Phone1.setType(Phone.Type.HOME);
        Phone user1Phone2 = new Phone();
        user1Phone2.setNumber("111-111");
        user1Phone2.setType(Phone.Type.MOBILE);
        userDTOrq1.setPhones(List.of(user1Phone1, user1Phone2));

        userDTOrq2 = new UserDTOrq();
        userDTOrq2.setFirstName("David");
        userDTOrq2.setLastName("Morales");
        userDTOrq2.setBirthDate(LocalDate.of(2000, 5, 23));
        userDTOrq2.setEmail("david.doe@ukr.net");
        Address address2 = new Address();
        address2.setCity("NightCity");
        address2.setStreet("321 Secondary St");
        address2.setNumber("66");
        address2.setApt("02");
        address2.setZip("44555");
        userDTOrq2.setAddress(address2);
        Phone user2Phone1 = new Phone();
        user2Phone1.setNumber("222-222");
        user2Phone1.setType(Phone.Type.WORK);
        userDTOrq1.setPhones(List.of(user2Phone1));
    }

    @Test
    @Order(1)
    void testCreateUser() throws Exception {
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTOrq1)));

        response.andExpect(MockMvcResultMatchers.status().isCreated());


    }

    @Test
    @Order(2)
    void testUpdateUser() {

    }

    @Test
    @Order(3)
    void testPatchUser() {

    }

    @Test
    @Order(4)
    void testDeleteUser() {

    }

    @Test
    @Order(5)
    void testSearchUser() {

    }
}
