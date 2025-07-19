package com.ts.ts_profile_engine_1676.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @Column(name = "project_id")
    private String projectId;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "project_owner")
    private String projectOwner;
}