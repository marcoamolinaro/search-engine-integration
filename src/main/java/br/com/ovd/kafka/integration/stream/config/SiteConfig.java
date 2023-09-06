package br.com.ovd.kafka.integration.stream.config;

import java.util.List;

public interface SiteConfig {
    String apiKey();
    String secret();
    List<String> orgs();
}
