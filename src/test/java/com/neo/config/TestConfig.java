package com.neo.config;


import com.neo.repository.UserRepository;
import com.neo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
@RequiredArgsConstructor
public class TestConfig {

    @Bean
    public ModelMapper testModelMapper() {
        ModelMapper mm = new ModelMapper();

        mm.getConfiguration().setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setPropertyCondition(u -> u.getSource() != null);  // Skip properties with null value

        return mm;
    }

    @Bean
    public UserService testUserService() {
        return new UserService(Mockito.mock(UserRepository.class), testModelMapper());
    }
}
