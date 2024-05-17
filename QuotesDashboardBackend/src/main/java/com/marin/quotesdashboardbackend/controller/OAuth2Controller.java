package com.marin.quotesdashboardbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuth2Controller {

    @GetMapping("login/oauth2/code/google")
    @ResponseBody
    public String handleOAuth2Code(@RequestParam("code") String code) {
        // This will display the code in the browser
        return "Authorization Code: " + code;
    }
}
