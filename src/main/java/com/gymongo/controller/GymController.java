package com.gymongo.controller;

import com.gymongo.entity.Gym;
import com.gymongo.repository.GymRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gyms")
public class GymController {

    private final GymRepository gymRepository;

    public GymController(GymRepository gymRepository) {
        this.gymRepository = gymRepository;
    }

    @GetMapping
    public List<Gym> list() {
        return gymRepository.findAll();
    }

    @PostMapping
    public Gym create(@RequestBody Gym gym) {
        return gymRepository.save(gym);
    }
}
