package com.example.hospitalemr.repository;

import com.example.hospitalemr.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findAllByOrderByPostedAtDesc();
}
