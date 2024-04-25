package com.example.week4_redis;

import com.example.week4_redis.models.User;
import com.example.week4_redis.repositories.UserRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
public class Week4RedisApplication {
    @Autowired
    private UserRepository userRepository;
    public static void main(String[] args) {
        SpringApplication.run(Week4RedisApplication.class, args);
    }
    @Bean
    CommandLineRunner commandLineRunner(){
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                Faker faker = new Faker();
                for (int i = 0; i<10; i++){
                    userRepository.save(new User(i+1, faker.name().fullName()));
                }
            }
        };
    }
}
