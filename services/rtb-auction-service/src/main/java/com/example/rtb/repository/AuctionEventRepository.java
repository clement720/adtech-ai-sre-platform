package com.example.rtb.repository;

import com.example.rtb.entity.AuctionEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionEventRepository extends JpaRepository<AuctionEvent, Long> {
}
