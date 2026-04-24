package com.example.rtb.web;

import com.example.rtb.entity.Campaign;
import com.example.rtb.repository.CampaignRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CampaignController {

    private final CampaignRepository campaignRepository;

    public CampaignController(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    @GetMapping("/campaigns")
    public String listCampaigns(Model model) {
        model.addAttribute("campaigns", campaignRepository.findAll());
        return "campaigns";
    }

    @GetMapping("/campaigns/new")
    public String newCampaign(Model model) {
        model.addAttribute("campaign", new Campaign());
        return "campaign-form";
    }

    @PostMapping("/campaigns")
    public String saveCampaign(@ModelAttribute Campaign campaign) {
        campaignRepository.save(campaign);
        return "redirect:/campaigns";
    }
}
