package com.example.rtb.entity;

import jakarta.persistence.*;

@Entity
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String advertiser;
    private String targetCountry;
    private String targetDevice;
    private String targetContentType;
    private double maxBid;
    private double dailyBudget;
    private double spentToday;
    private boolean active;

    public Long getId() { return id; }

    public String getAdvertiser() { return advertiser; }
    public void setAdvertiser(String advertiser) { this.advertiser = advertiser; }

    public String getTargetCountry() { return targetCountry; }
    public void setTargetCountry(String targetCountry) { this.targetCountry = targetCountry; }

    public String getTargetDevice() { return targetDevice; }
    public void setTargetDevice(String targetDevice) { this.targetDevice = targetDevice; }

    public String getTargetContentType() { return targetContentType; }
    public void setTargetContentType(String targetContentType) { this.targetContentType = targetContentType; }

    public double getMaxBid() { return maxBid; }
    public void setMaxBid(double maxBid) { this.maxBid = maxBid; }

    public double getDailyBudget() { return dailyBudget; }
    public void setDailyBudget(double dailyBudget) { this.dailyBudget = dailyBudget; }

    public double getSpentToday() { return spentToday; }
    public void setSpentToday(double spentToday) { this.spentToday = spentToday; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
