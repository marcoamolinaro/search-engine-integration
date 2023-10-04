package br.com.ovd.kafka.integration.stream.config;

import io.smallrye.config.ConfigMapping;

import java.util.List;

@ConfigMapping(prefix = "integration.sites[0]")
public interface SiteConfig {
    String site();
    String key();
    String secret();
    List<String> orgs();
}
