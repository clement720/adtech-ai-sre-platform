package com.example.rtb.model;

public class AuctionResponse {

    private String auctionId;
    private String decision;
    private double bidPrice;
    private String advertiser;
    private long latencyMs;
    private String reason;

    public AuctionResponse(String auctionId, String decision, double bidPrice,
                           String advertiser, long latencyMs, String reason) {
        this.auctionId = auctionId;
        this.decision = decision;
        this.bidPrice = bidPrice;
        this.advertiser = advertiser;
        this.latencyMs = latencyMs;
        this.reason = reason;
    }

    public String getAuctionId() { return auctionId; }
    public String getDecision() { return decision; }
    public double getBidPrice() { return bidPrice; }
    public String getAdvertiser() { return advertiser; }
    public long getLatencyMs() { return latencyMs; }
    public String getReason() { return reason; }
}
