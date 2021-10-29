package com.scut.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {

    /**
     * 测试连接
     *
     * @return
     */
    @GetMapping("/info")
    public String hello() {
        return "sec-kill project!";
    }
}
