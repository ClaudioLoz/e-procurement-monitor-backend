package com.osce.eprocurementmonitorbackend.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE auth_user SET active = false WHERE id=?")
@Where(clause = "active=true")
public class AuthUser extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String nin;
    @Column(nullable = false)//, unique = true)
    private String email;
    private String password;

}
