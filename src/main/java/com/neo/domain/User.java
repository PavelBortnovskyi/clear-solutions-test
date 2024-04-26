package com.neo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", allocationSize = 1)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "email", nullable = false)
    private String email;

    @Embedded
    private Address address;

    @ElementCollection
    @CollectionTable(name = "user_phones", joinColumns = @JoinColumn(name = "user_id"),
                     indexes = @Index(name = "user_phones_user_id_idx", columnList = "user_id"))
    @Setter(AccessLevel.PRIVATE)
    private List<Phone> phones = new ArrayList<>();

    public void addPhone(Phone phone) {
        this.phones.add(phone);
    }
}
