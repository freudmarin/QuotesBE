package com.marin.quotesdashboardbackend.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/home")
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    @PreAuthorize("hasRole('USER')")
    @GetMapping("")
    public String test() {
        log.info("test");
        return "test endpoint";
    }

    @GetMapping("/test2")
    public String test2() {
        log.info("test2");
        return "test2 endpoint";
    }
}
