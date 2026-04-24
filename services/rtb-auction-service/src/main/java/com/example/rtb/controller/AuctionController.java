package com.example.rtb.controller;

import org.springframework.web.bind.annotation.*;
import io.micrometer.core.instrument.*;
import java.util.Random;

@RestController
public class AuctionController {

    private final Random random = new Random();

    private final Counter bidCounter;
    private final Counter noBidCounter;
    private final Timer auctionLatencyTimer;

    public AuctionController(MeterRegistry registry) {
        this.bidCounter = Counter.builder("rtb_bid_total")
                .description("Total successful RTB bid responses")
                .register(registry);

        this.noBidCounter = Counter.builder("rtb_no_bid_total")
                .description("Total RTB no-bid responses")
                .register(registry);

        this.auctionLatencyTimer = Timer.builder("rtb_auction_latency")
                .description("RTB auction latency")
                .register(registry);
    }

    @GetMapping("/health")
    public String health() {
        return "RTB Auction Service is UP";
    }

    @PostMapping("/auction")
    public String auction() {
        return auctionLatencyTimer.record(() -> {
            try {
                int latency = random.nextInt(150);
                Thread.sleep(latency);

                if (random.nextDouble() < 0.3) {
                    noBidCounter.increment();
                    return "NO_BID";
                }

                bidCounter.increment();
                return "BID_RESPONSE";

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
    }
}