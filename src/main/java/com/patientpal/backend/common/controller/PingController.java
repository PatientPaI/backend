package com.patientpal.backend.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @GetMapping("/ping")
    public String pong() {
        return "pong";
    }

    @GetMapping("/healthcheck")
    public String healthcheck() {
        return "OK";
    }

}
