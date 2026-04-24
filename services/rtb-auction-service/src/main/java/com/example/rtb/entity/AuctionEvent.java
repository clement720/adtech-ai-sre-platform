package com.example.rtb.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AuctionEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String auctionId;
    private String viewerId;
    private String country;
    private String deviceType;
    private String contentType;
    private boolean liveEvent;
    private String adSlot;
    private String decision;
    private double bidPrice;
    private String advertiser;
    private long latencyMs;
    private String reason;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }

    public String getAuctionId() { return auctionId; }
    public void setAuctionId(String auctionId) { this.auctionId = auctionId; }

    public String getViewerId() { return viewerId; }
    public void setViewerId(String viewerId) { this.viewerId = viewerId; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public boolean isLiveEvent() { return liveEvent; }
    public void setLiveEvent(boolean liveEvent) { this.liveEvent = liveEvent; }

    public String getAdSlot() { return adSlot; }
    public void setAdSlot(String adSlot) { this.adSlot = adSlot; }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public double getBidPrice() { return bidPrice; }
    public void setBidPrice(double bidPrice) { this.bidPrice = bidPrice; }

    public String getAdvertiser() { return advertiser; }
    public void setAdvertiser(String advertiser) { this.advertiser = advertiser; }

    public long getLatencyMs() { return latencyMs; }
    public void setLatencyMs(long latencyMs) { this.latencyMs = latencyMs; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
