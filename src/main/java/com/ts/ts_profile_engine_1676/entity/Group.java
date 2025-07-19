package com.ts.ts_profile_engine_1676.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "group_table") // Avoid conflict with SQL reserved word
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group {

    @Id
    @Column(name = "group_id")
    private String groupId;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "group_owner")
    private String groupOwner;
}
