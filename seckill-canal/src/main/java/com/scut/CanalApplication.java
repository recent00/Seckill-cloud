package com.scut;

import com.scut.consumer.Comsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableDiscoveryClient
public class CanalApplication {
    @Autowired
    private Comsumer comsumer;

/*    @PostConstruct
    public void start() {
        comsumer.start();
    }*/
    public static void main(String[] args) {

        SpringApplication.run(CanalApplication.class, args);
    }
}
