package com.example.rtb.controller;

import com.example.rtb.model.AuctionRequest;
import com.example.rtb.model.AuctionResponse;
import org.springframework.web.bind.annotation.*;
import io.micrometer.core.instrument.*;

import java.util.Random;
import java.util.UUID;

@RestController
public class AuctionController {

    private final Random random = new Random();

    private final Counter bidCounter;
    private final Counter noBidCounter;
    private final Counter timeoutCounter;
    private final Counter liveEventCounter;
    private final Timer auctionLatencyTimer;

    public AuctionController(MeterRegistry registry) {
        this.bidCounter = Counter.builder("rtb_bid_total").register(registry);
        this.noBidCounter = Counter.builder("rtb_no_bid_total").register(registry);
        this.timeoutCounter = Counter.builder("rtb_timeout_total").register(registry);
        this.liveEventCounter = Counter.builder("rtb_live_event_requests_total").register(registry);
        this.auctionLatencyTimer = Timer.builder("rtb_auction_latency").register(registry);
    }

    @GetMapping("/health")
    public String health() {
        return "RTB Auction Service is UP";
    }

    @PostMapping("/auction")
    public AuctionResponse auction(@RequestBody(required = false) AuctionRequest request) {

        if (request == null) {
            request = new AuctionRequest();
            request.setCountry("US");
            request.setDeviceType("CTV");
            request.setContentType("VOD");
            request.setLiveEvent(false);
            request.setAdSlot("pre-roll");
        }

        AuctionRequest finalRequest = request;
        long start = System.currentTimeMillis();

        return auctionLatencyTimer.record(() -> {
            try {
                if (finalRequest.isLiveEvent()) {
                    liveEventCounter.increment();
                }

                int latency = random.nextInt(150);
                Thread.sleep(latency);

                long totalLatency = System.currentTimeMillis() - start;

                if (latency > 120) {
                    timeoutCounter.increment();
                    return new AuctionResponse(UUID.randomUUID().toString(), "TIMEOUT", 0.0, "NONE", totalLatency, "Timeout");
                }

                if (random.nextDouble() < 0.3) {
                    noBidCounter.increment();
                    return new AuctionResponse(UUID.randomUUID().toString(), "NO_BID", 0.0, "NONE", totalLatency, "No matching campaign");
                }

                bidCounter.increment();
                return new AuctionResponse(UUID.randomUUID().toString(), "BID",
                        Math.round((1 + random.nextDouble() * 4) * 100.0) / 100.0,
                        "Nike", totalLatency, "Ad eligible");

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
    }
}
