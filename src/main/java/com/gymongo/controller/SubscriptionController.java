package com.gymongo.controller;

import com.gymongo.entity.Subscription;
import com.gymongo.repository.SubscriptionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionController(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @GetMapping
    public List<Subscription> list() {
        return subscriptionRepository.findAll();
    }

    @PostMapping
    public Subscription create(@RequestBody Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }
}
