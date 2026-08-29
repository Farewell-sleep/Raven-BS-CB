package com.alan.clients.util.file.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConfigManager {
    private final List<ConfigFile> configs = new ArrayList<>();

    public void update() {
    }

    public ConfigFile getConfigfile() {
        return new ConfigFile();
    }

    public void forEach(Consumer<ConfigFile> consumer) {
        for (ConfigFile c : this.configs) consumer.accept(c);
    }
}
