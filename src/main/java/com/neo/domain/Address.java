package com.neo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import lombok.ToString;

@ToString
@Embeddable
public class Address {

    @JsonProperty("city")
    private String city;

    @JsonProperty("street")
    private String street;

    @JsonProperty("number")
    private String number;

    @JsonProperty("apt")
    private String apt;

    @JsonProperty("zip")
    private String zip;
}
