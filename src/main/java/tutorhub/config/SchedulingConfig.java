package tutorhub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Turns on @Scheduled (cron jobs) and @Async (background method execution).
 */
@Configuration
@EnableScheduling
@EnableAsync
public class SchedulingConfig {
}