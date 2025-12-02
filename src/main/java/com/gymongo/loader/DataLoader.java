package com.gymongo.loader;

import com.gymongo.entity.Gym;
import com.gymongo.entity.User;
import com.gymongo.repository.GymRepository;
import com.gymongo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final GymRepository gymRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(GymRepository gymRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.gymRepository = gymRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (gymRepository.count() == 0) {
            Gym g = new Gym();
            g.setName("Downtown Gym");
            g.setAddress("123 Main St");
            g.setCapacity(30);
            gymRepository.save(g);
        }

        if (userRepository.count() == 0) {
            User u = new User();
            u.setUsername("admin");
            u.setPassword(passwordEncoder.encode("password"));
            u.setFullName("Admin User");
            u.setRole("ADMIN");
            userRepository.save(u);
        }
    }
}
