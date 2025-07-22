package com.newez.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "live_channels")
@Getter
@Setter
public class LiveChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "display_order")
    private int displayOrder;

    @Column(name = "channel_name")
    private String channelName;

    @Column(name = "channel_title")
    private String channelTitle;

    @Column(name = "stream_url")
    private String streamUrl;
}