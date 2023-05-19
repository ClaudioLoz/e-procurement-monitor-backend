package com.osce.eprocurementmonitorbackend.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;


import javax.persistence.*;

@Entity
@Setter
@Getter
@SQLDelete(sql = "UPDATE file_info SET active = false WHERE id=?")
@Where(clause = "active=true")
public class FileInfo extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    private String hash;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "e_procurement_id", nullable = false)
    private EProcurement eprocurement;

}
