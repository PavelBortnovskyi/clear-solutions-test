package com.neo.dto.rs;

import com.neo.domain.Address;
import com.neo.domain.Phone;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class UserDTOrs {

    private String firstName;

    private String lastName;

    private LocalDate birthDate;

    private String email;

    private Address address;

    private List<Phone> phones;
}