package com.example.campusdashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面控制器
 */
@Controller
public class PageController {
    
    /**
     * 首页映射
     */
    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
}
