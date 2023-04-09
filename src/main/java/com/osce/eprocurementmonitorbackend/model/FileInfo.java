package com.osce.eprocurementmonitorbackend.model;

import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;

@Entity
@Setter
@Getter
public class FileInfo extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "e_procurement_id", nullable = false)
    private EProcurement eprocurement;

}
