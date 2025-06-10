package com.example.hospitalemr.service;

import com.example.hospitalemr.domain.Notice;
import com.example.hospitalemr.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {
    @Autowired
    private NoticeRepository noticeRepository;

    public List<Notice> getAllNotices() {
        return noticeRepository.findAllByOrderByPostedAtDesc();
    }

    public Notice getNotice(Long id) {
        return noticeRepository.findById(id).orElse(null);
    }
}
