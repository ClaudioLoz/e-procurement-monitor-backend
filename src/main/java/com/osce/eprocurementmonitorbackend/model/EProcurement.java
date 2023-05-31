package com.osce.eprocurementmonitorbackend.model;

import com.osce.eprocurementmonitorbackend.enums.ProcurementObject;
import com.osce.eprocurementmonitorbackend.enums.ProcurementStatus;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE eprocurement SET active = false WHERE id=?")
@Where(clause = "active=true")
public class EProcurement extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private AuthUser user;

    @Enumerated
    @Column(nullable = false)
    private ProcurementStatus procurementStatus = ProcurementStatus.FOLLOW_UP;
    @Column(nullable = false)
    private String contractingEntityName;
    @Column(nullable = false)
    private String contractingEntityRuc;
    @Column(nullable = false)
    private String contractorName;
    @Column(nullable = false)
    private String contractorRuc;
    @Enumerated
    @Column(nullable = false)
    private ProcurementObject procurementObject;
    @Column(nullable = false)
    private Double amount;
    @Column(nullable = false)
    private String department;
    private String province;
    private String district;
    @Column(nullable = false)
    private LocalDate contractStartDate;
    @Column(nullable = false)
    private LocalDate contractEndDate;

    @Column(columnDefinition = "TEXT")
    private String description;

}
