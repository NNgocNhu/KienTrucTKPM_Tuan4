package com.example.week4_redis.controllers;

import com.example.week4_redis.models.User;
import com.example.week4_redis.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    private Jedis jedis = new Jedis();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/users")
    public User addEmployee(@RequestBody User user) {
        jedis.set(String.valueOf(user.getId()), user.getName());
        System.out.println("saved in cache");
        return userRepository.save(user);
    }

    @GetMapping("/users/{userId}")
    public User findUserByID(@PathVariable(value = "userId") long userId) {
        String key = String.valueOf(userId);

        if (jedis.exists(key)) {
            User userInCache = new User();
            userInCache.setId(userId);
            System.out.println("fetching from cache" + userId);
            String userName = jedis.get(key);
            userInCache.setName(userName);
            return userInCache;
        } else {
            System.out.println("fetching from database" + userId);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User_id " + userId + " not found"));
            jedis.set(key, user.getName());
            System.out.println("saved in cache");
            return user;
        }
    }


    @PutMapping("/users/{userId}")
    public User updateUser(@PathVariable(value = "userId") long userId, @RequestBody User user){
        User userUpdate = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found"));
        userUpdate.setName(user.getName());
        jedis.set(String.valueOf(user.getId()), user.getName());
        System.out.println("saved in cache");
        return userRepository.save(userUpdate);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable(value = "userId") long userId){
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found"));
        userRepository.delete(user);
        jedis.del(String.valueOf(user.getId()));
        System.out.println("Delete in cache");
        System.out.println("Delete complete!");
    }
}
