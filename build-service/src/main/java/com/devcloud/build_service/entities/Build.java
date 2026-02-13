package com.devcloud.build_service.entities;


import com.devcloud.build_service.enums.BuildStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "builds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Build {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(nullable = false)
    private String environment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BuildStatus status;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @OneToMany(
            mappedBy = "build",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<BuildLog> logs = new ArrayList<>();


    @Column(name = "repo_url", nullable = false)
    private String repoUrl;

    @Column(name = "branch")
    private String branch;

}
