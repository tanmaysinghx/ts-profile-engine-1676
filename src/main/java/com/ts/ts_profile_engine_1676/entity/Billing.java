package com.ts.ts_profile_engine_1676.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "billing")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Billing {

    @Id
    @Column(name = "billing_id")
    private String billingId;

    @Column(name = "billing_code")
    private String billingCode;

    @Column(name = "description")
    private String description;
}
