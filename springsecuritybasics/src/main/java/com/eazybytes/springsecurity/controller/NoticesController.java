package com.eazybytes.springsecurity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NoticesController
{


    @GetMapping("/notices")
    public String getNotice() {
        return "Notice";
    }
}

