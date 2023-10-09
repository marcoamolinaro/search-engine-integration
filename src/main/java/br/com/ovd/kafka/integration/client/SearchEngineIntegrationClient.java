package br.com.ovd.kafka.integration.client;

import br.com.ovd.kafka.integration.model.SearchEngineIntegrationRequest;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchEngineIntegrationClient {

    private String url;
    private SearchEngineIntegrationRequest engineIntegrationRequest;

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .executor(executorService)
            .version(HttpClient.Version.HTTP_2)
            .build();

    public CompletionStage<Long> alterar(String id) {
        return this.httpClient.sendAsync(
                HttpRequest
                        .newBuilder()
                        .PUT(HttpRequest.BodyPublishers.ofString(engineIntegrationRequest.toString()))
                        .uri(URI.create(url + "/" + id))
                        .header("Accept", "application/json").build(),
                HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(Long::parseLong)
                .toCompletableFuture();
    }

    public CompletionStage<Long> salvar(String id) {
        return this.httpClient.sendAsync(
                        HttpRequest
                                .newBuilder()
                                .POST(HttpRequest.BodyPublishers.ofString(engineIntegrationRequest.toString()))
                                .uri(URI.create(url + "/" + id))
                                .header("Accept", "application/json").build(),
                        HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(Long::parseLong)
                .toCompletableFuture();
    }
}