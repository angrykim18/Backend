package com.newez.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_groups")
@Getter
@Setter
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name", unique = true, nullable = false)
    private String groupName;

    private String description;

    @Column(name = "live_server_id")
    private Long liveServerId;

    @Column(name = "vod_server_id")
    private Long vodServerId;

    @Column(name = "replay_server_id")
    private Long replayServerId;
}