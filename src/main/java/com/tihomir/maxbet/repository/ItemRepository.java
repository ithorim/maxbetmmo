package com.tihomir.maxbet.repository;

import com.tihomir.maxbet.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Integer> {
}
