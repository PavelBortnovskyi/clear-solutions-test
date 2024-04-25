package com.neo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.ToString;

@ToString
@Embeddable
public class Phone {

    @JsonProperty("number")
    private String number;

    @JsonProperty("type")
    @Enumerated(EnumType.STRING)
    private Type type;

    enum Type {
        HOME, WORK, MOBILE
    }
}
