package com.example.campusdashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面控制器
 * 注意：前端已迁移至 Vue 3 应用（vue-frontend），独立部署在 5174 端口
 */
@Controller
public class PageController {
    
    /**
     * 首页映射 - 重定向到 Vue 应用
     * Vue 应用独立部署，可通过 http://localhost:5174 访问
     */
    @GetMapping("/")
    public String index() {
        // 重定向到 Vue 应用（开发环境）
        return "redirect:http://localhost:5174";
    }
}
