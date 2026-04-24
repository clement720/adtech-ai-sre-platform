package com.example.rtb.model;

public class AuctionRequest {
    private String viewerId;
    private String country;
    private String deviceType;
    private String contentType;
    private boolean liveEvent;
    private String adSlot;

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
}
