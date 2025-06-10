package com.example.hospitalemr.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Notice {
    @Id
    @GeneratedValue
    private Long noticeId;

    private String title;
    private String content;
    private LocalDateTime postedAt;
    private String writer;
    private String type;
}