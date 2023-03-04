package ru.practicum.serv;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.StatistClient;

@Configuration
public class ConfigStatistClient {
    private final String serverUrl;

    public ConfigStatistClient(@Value("${stats-service.url}") String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Bean
    public StatistClient createStatistConfigClient() {
        return new StatistClient(serverUrl);
    }
}
