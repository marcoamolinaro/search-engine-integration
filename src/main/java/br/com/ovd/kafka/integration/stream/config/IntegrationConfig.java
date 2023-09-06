package br.com.ovd.kafka.integration.stream.config;

import io.smallrye.config.ConfigMapping;

import java.util.Map;

@ConfigMapping(prefix = "integration")
public interface IntegrationConfig {
    String searchEngineUrl = null;
    Map<String, SiteConfig> sites();
}
