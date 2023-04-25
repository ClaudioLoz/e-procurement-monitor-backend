package com.osce.eprocurementmonitorbackend.api.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;


@Getter
@Setter
public class LoginInDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
