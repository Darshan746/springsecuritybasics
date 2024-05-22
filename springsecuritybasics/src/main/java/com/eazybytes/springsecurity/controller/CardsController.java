package com.eazybytes.springsecurity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CardsController {


    @GetMapping("/myCards")
    public String getCardsDetail() {
        return "My cards:";
    }
}
