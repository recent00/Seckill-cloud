package com.scut.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.scut.common.RespBean;
import com.scut.common.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class testController {

    /**
     * 测试连接
     *
     * @return
     */
    @GetMapping("/info")
    @SentinelResource(value = "hello",blockHandler = "handleException1")
    public String hello() {
        return "sec-kill project!";
    }

    public String handleException1(BlockException exception) {
        log.info("访问过于频繁，请稍后再试");
        return "访问过于频繁";
    }
}
