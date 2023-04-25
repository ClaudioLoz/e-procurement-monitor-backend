package com.osce.eprocurementmonitorbackend.api.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class JwtOutDTO {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    
    private String name;

    public JwtOutDTO(String accessToken, Long id, String username, String email, String name) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
    }
}
