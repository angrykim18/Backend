package com.newez.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Table(name = "app_versions")
@Getter
@Setter
public class AppVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "version_code", unique = true, nullable = false)
    private int versionCode;

    @Column(name = "version_name", nullable = false)
    private String versionName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "release_notes")
    private String releaseNotes;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "uploaded_at", updatable = false)
    private Date uploadedAt;
}