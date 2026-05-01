package com.techmagnet.scm.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class DbKeepAliveService {

    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.keepalive.enabled:true}")
    private boolean enabled;

    // Cron expression is referenced directly on the @Scheduled annotation.

    // number of quick retries on failure
    @Value("${spring.keepalive.retries:2}")
    private int retries;

    public DbKeepAliveService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // default: every 6 days at midnight (cron expression). To change interval, edit application.properties
    @Scheduled(cron = "${spring.keepalive.cron:0 0 0 */6 * *}")
    public void keepAlive() {
        if (!enabled) {
            log.debug("DbKeepAliveService is disabled via configuration.");
            return;
        }

        log.info("DbKeepAliveService triggered at {} — executing lightweight ping.", Instant.now());

        int attempt = 0;
        while (attempt <= retries) {
            try {
                attempt++;
                Integer result = this.jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                log.info("DB keep-alive succeeded on attempt {}. Result={}", attempt, result);
                return;
            } catch (Exception e) {
                log.warn("DB keep-alive attempt {} failed: {}", attempt, e.getMessage());
                if (attempt > retries) {
                    log.error("DB keep-alive failed after {} attempts", attempt, e);
                } else {
                    try {
                        Thread.sleep(1000L * attempt); // small backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.warn("Keep-alive retry sleep interrupted");
                        return;
                    }
                }
            }
        }
    }

}


