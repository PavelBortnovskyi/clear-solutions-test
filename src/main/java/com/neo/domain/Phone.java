package com.neo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@Data
@NoArgsConstructor
public class Phone {

    @JsonProperty("number")
    private String number;

    @JsonProperty("type")
    @Enumerated(EnumType.STRING)
    private Type type;

    public enum Type {
        HOME, WORK, MOBILE
    }
}
