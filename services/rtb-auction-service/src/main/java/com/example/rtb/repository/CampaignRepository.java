package com.example.rtb.repository;

import com.example.rtb.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    List<Campaign> findByActiveTrueAndTargetCountryAndTargetDeviceAndTargetContentType(
            String targetCountry,
            String targetDevice,
            String targetContentType
    );
}
