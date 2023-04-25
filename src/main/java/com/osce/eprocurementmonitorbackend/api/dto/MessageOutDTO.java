package com.osce.eprocurementmonitorbackend.api.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MessageOutDTO {
    private String message;

    public MessageOutDTO(String message) {
        this.message = message;
    }
}
