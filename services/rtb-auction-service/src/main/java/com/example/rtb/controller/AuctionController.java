package com.example.rtb.controller;

import com.example.rtb.entity.AuctionEvent;
import com.example.rtb.entity.Campaign;
import com.example.rtb.model.AuctionRequest;
import com.example.rtb.model.AuctionResponse;
import com.example.rtb.repository.AuctionEventRepository;
import com.example.rtb.repository.CampaignRepository;
import com.example.rtb.service.CampaignCacheService;
import io.micrometer.core.instrument.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@RestController
public class AuctionController {

    private final Random random = new Random();
    private final CampaignRepository campaignRepository;
    private final AuctionEventRepository auctionEventRepository;
    private final CampaignCacheService cacheService;

    private final Counter bidCounter;
    private final Counter noBidCounter;
    private final Counter timeoutCounter;
    private final Counter liveEventCounter;
    private final Counter cacheHitCounter;
    private final Counter cacheMissCounter;
    private final Timer auctionLatencyTimer;

    public AuctionController(
            MeterRegistry registry,
            CampaignRepository campaignRepository,
            AuctionEventRepository auctionEventRepository,
            CampaignCacheService cacheService
    ) {
        this.campaignRepository = campaignRepository;
        this.auctionEventRepository = auctionEventRepository;
        this.cacheService = cacheService;

        this.bidCounter = Counter.builder("rtb_bid_total").register(registry);
        this.noBidCounter = Counter.builder("rtb_no_bid_total").register(registry);
        this.timeoutCounter = Counter.builder("rtb_timeout_total").register(registry);
        this.liveEventCounter = Counter.builder("rtb_live_event_requests_total").register(registry);
        this.cacheHitCounter = Counter.builder("rtb_cache_hit_total").register(registry);
        this.cacheMissCounter = Counter.builder("rtb_cache_miss_total").register(registry);
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
            request.setViewerId("viewer-default");
            request.setCountry("US");
            request.setDeviceType("CTV");
            request.setContentType("live-sports");
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

                Thread.sleep(random.nextInt(80) + 20);

                long latencyMs = System.currentTimeMillis() - start;

                if (latencyMs > 120) {
                    timeoutCounter.increment();
                    AuctionResponse response = new AuctionResponse(
                            UUID.randomUUID().toString(),
                            "TIMEOUT",
                            0.0,
                            "NONE",
                            latencyMs,
                            "Auction exceeded RTB latency threshold"
                    );
                    saveAuctionEvent(finalRequest, response);
                    return response;
                }

                String cacheKey = cacheService.buildKey(
                        finalRequest.getCountry(),
                        finalRequest.getDeviceType(),
                        finalRequest.getContentType()
                );

                Optional<String> cachedCampaignId = cacheService.getCampaignId(cacheKey);

                Campaign selectedCampaign = null;

                if (cachedCampaignId.isPresent()) {
                    cacheHitCounter.increment();

                    if ("NONE".equals(cachedCampaignId.get())) {
                        noBidCounter.increment();
                        AuctionResponse response = new AuctionResponse(
                                UUID.randomUUID().toString(),
                                "NO_BID",
                                0.0,
                                "NONE",
                                latencyMs,
                                "No matching campaign from Redis cache"
                        );
                        saveAuctionEvent(finalRequest, response);
                        return response;
                    }

                    selectedCampaign = campaignRepository
                            .findById(Long.valueOf(cachedCampaignId.get()))
                            .orElse(null);
                } else {
                    cacheMissCounter.increment();

                    List<Campaign> matchingCampaigns =
                            campaignRepository.findByActiveTrueAndTargetCountryAndTargetDeviceAndTargetContentType(
                                    finalRequest.getCountry(),
                                    finalRequest.getDeviceType(),
                                    finalRequest.getContentType()
                            );

                    if (matchingCampaigns.isEmpty()) {
                        cacheService.cacheNoMatch(cacheKey);
                        noBidCounter.increment();

                        AuctionResponse response = new AuctionResponse(
                                UUID.randomUUID().toString(),
                                "NO_BID",
                                0.0,
                                "NONE",
                                latencyMs,
                                "No matching campaign"
                        );
                        saveAuctionEvent(finalRequest, response);
                        return response;
                    }

                    selectedCampaign = matchingCampaigns.get(0);
                    cacheService.cacheCampaignId(cacheKey, selectedCampaign.getId());
                }

                if (selectedCampaign == null) {
                    noBidCounter.increment();
                    AuctionResponse response = new AuctionResponse(
                            UUID.randomUUID().toString(),
                            "NO_BID",
                            0.0,
                            "NONE",
                            latencyMs,
                            "Cached campaign not found in database"
                    );
                    saveAuctionEvent(finalRequest, response);
                    return response;
                }

                if (selectedCampaign.getSpentToday() >= selectedCampaign.getDailyBudget()) {
                    noBidCounter.increment();
                    AuctionResponse response = new AuctionResponse(
                            UUID.randomUUID().toString(),
                            "NO_BID",
                            0.0,
                            selectedCampaign.getAdvertiser(),
                            latencyMs,
                            "Campaign budget exhausted"
                    );
                    saveAuctionEvent(finalRequest, response);
                    return response;
                }

                double bidPrice = Math.round(
                        (selectedCampaign.getMaxBid() * (0.7 + random.nextDouble() * 0.3)) * 100.0
                ) / 100.0;

                selectedCampaign.setSpentToday(selectedCampaign.getSpentToday() + bidPrice);
                campaignRepository.save(selectedCampaign);

                bidCounter.increment();

                AuctionResponse response = new AuctionResponse(
                        UUID.randomUUID().toString(),
                        "BID",
                        bidPrice,
                        selectedCampaign.getAdvertiser(),
                        latencyMs,
                        "Eligible campaign matched"
                );

                saveAuctionEvent(finalRequest, response);
                return response;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
    }

    private void saveAuctionEvent(AuctionRequest request, AuctionResponse response) {
        AuctionEvent event = new AuctionEvent();
        event.setAuctionId(response.getAuctionId());
        event.setViewerId(request.getViewerId());
        event.setCountry(request.getCountry());
        event.setDeviceType(request.getDeviceType());
        event.setContentType(request.getContentType());
        event.setLiveEvent(request.isLiveEvent());
        event.setAdSlot(request.getAdSlot());
        event.setDecision(response.getDecision());
        event.setBidPrice(response.getBidPrice());
        event.setAdvertiser(response.getAdvertiser());
        event.setLatencyMs(response.getLatencyMs());
        event.setReason(response.getReason());

        auctionEventRepository.save(event);
    }
}
