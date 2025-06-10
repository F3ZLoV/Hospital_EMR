package com.example.hospitalemr.controller;

import com.example.hospitalemr.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping
    public String noticeList(Model model) {
        model.addAttribute("notices", noticeService.getAllNotices());
        return "notice_list"; // notice_list.html
    }

    @GetMapping("/{id}")
    public String noticeDetail(@PathVariable Long id, Model model) {
        model.addAttribute("notice", noticeService.getNotice(id));
        return "notice_detail"; // notice_detail.html
    }
}

