package com.ridelink.account_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCheckRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseCheckRunner.class);
    private final MongoTemplate mongoTemplate;

    public DatabaseCheckRunner(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(String... args) {
        String dbName = mongoTemplate.getDb().getName();
        log.info("############################################################");
        log.info("## ACCOUNT SERVICE CONNECTED TO DATABASE: '{}'", dbName);
        log.info("############################################################");
    }
}
