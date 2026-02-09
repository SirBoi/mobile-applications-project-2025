package com.project2025.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2025.model.Config;
import com.project2025.repository.ConfigRepository;

@Service
public class ConfigService {

    // Config in your model is a singleton row (id = 1)
    private static final Integer CONFIG_ID = 1;

    private final ConfigRepository configRepository;

    public ConfigService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Transactional
    public Config upsert(Config config) {
        // keep singleton id=1
        Config existing = configRepository.findById(CONFIG_ID).orElseGet(Config::new);
        existing.setStandardPrice(config.getStandardPrice());
        existing.setLuxuryPrice(config.getLuxuryPrice());
        existing.setVanPrice(config.getVanPrice());
        return configRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public Optional<Config> get() {
        return configRepository.findById(CONFIG_ID);
    }

    @Transactional
    public void delete() {
        configRepository.deleteById(CONFIG_ID);
    }
}
