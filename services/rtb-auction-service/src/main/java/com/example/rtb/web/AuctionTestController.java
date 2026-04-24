package com.example.rtb.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuctionTestController {

    @GetMapping("/auction-test")
    public String auctionTest() {
        return "auction-test";
    }
}
