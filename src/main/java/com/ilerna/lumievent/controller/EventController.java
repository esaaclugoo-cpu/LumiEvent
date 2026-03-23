package com.ilerna.lumievent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EventController {

        @GetMapping("/test")
        public String testPage() {
            return "index";
        }


    }

