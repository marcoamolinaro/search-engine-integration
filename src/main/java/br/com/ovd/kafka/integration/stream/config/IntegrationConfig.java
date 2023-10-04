package br.com.ovd.kafka.integration.stream.config;


import io.smallrye.config.ConfigMapping;

import java.util.List;

@ConfigMapping(prefix = "integration")
public interface IntegrationConfig {
    String searchEngineUrl();
    List<SiteConfig> sites();
}
