package ru.practicum.ewm;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.client.StatsClient;

@Configuration
public class EwnConfig {

    @Bean
    public StatsClient statsClient() {
        return new StatsClient(new RestTemplate());
    }
}
