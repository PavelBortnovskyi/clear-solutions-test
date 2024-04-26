package com.neo.dto.rq;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neo.domain.Address;
import com.neo.domain.Phone;
import com.neo.marker.Marker;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class UserDTOrq {

    @NotNull(message = "First name cannot be null! You have a name right?)",
            groups = Marker.NewOrUpdate.class)
    @Size(max = 20, min = 2, message = "First name length must be in range 2..20 characters",
            groups = {Marker.NewOrUpdate.class, Marker.PartialUpdate.class})
    private String firstName;

    @NotNull(message = "Last name cannot be null! We are serious people here)", groups = Marker.NewOrUpdate.class)
    @Size(max = 20, min = 2, message = "Last name length must be in range 2..20 characters",
            groups = {Marker.NewOrUpdate.class, Marker.PartialUpdate.class})
    private String lastName;

    @NotNull(message = "Sorry, but we need to know your age! How can we send you a gift without knowing this information?",
            groups = Marker.NewOrUpdate.class)
    @PastOrPresent(message = "Time traveler? At least world still exist in future. But use date earlier than present",
            groups = {Marker.NewOrUpdate.class, Marker.PartialUpdate.class})
    private LocalDate birthDate;

    @NotNull(message = "Sorry, but we need to know your email!", groups = Marker.NewOrUpdate.class)
    @Size(min = 6, max = 50, message = "Email length must be in range 6..50 characters",
            groups = {Marker.NewOrUpdate.class, Marker.PartialUpdate.class})
    @Pattern(regexp = "^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$", message = "Invalid email format",
            groups = {Marker.NewOrUpdate.class, Marker.PartialUpdate.class})
    private String email;

    @JsonProperty("address")
    private Address address;

    @JsonProperty("phones")
    private List<Phone> phones;
}