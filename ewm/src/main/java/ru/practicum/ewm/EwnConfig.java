package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.client.StatsClientImpl;

@Configuration
public class EwnConfig {

    @Bean
    public StatsClient statsClient() {
        return new StatsClient("http://localhost:9090", new RestTemplateBuilder());
    }
}
