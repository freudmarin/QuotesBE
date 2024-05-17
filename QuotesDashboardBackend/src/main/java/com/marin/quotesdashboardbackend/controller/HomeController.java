package com.marin.quotesdashboardbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/home")
@RequiredArgsConstructor
public class HomeController {

    /*@PreAuthorize("hasRole('ROLE_USER')")*/
    @GetMapping("")
    public String test() {
        return "test endpoint";
    }
}
