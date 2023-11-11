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
import java.util.concurrent.CompletableFuture;
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

    public CompletableFuture<String> alterar(String id) {
        String LOG_PATH = "[SearchEngineIntegrationClient->alterar] ";

        return this.httpClient.sendAsync(
                        HttpRequest.newBuilder()
                                .PUT(HttpRequest.BodyPublishers.ofString(engineIntegrationRequest.toJson()))
                                .uri(URI.create(url))
                                .header("accept", "text/plain")
                                .header("content-type", "application/json")
                                .build(),
                        HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(body -> LOG_PATH + "Sucesso ao criar ou atualizar produto na api de integração - " + body)
                .exceptionally(ex -> LOG_PATH + "Erro ao criar ou atualizar produto - " + ex.getMessage())
                .toCompletableFuture();
    }

    public CompletableFuture<String> salvar(String id) {
        String LOG_PATH = "[SearchEngineIntegrationClient->salvar] ";

        return this.httpClient.sendAsync(
                        HttpRequest
                                .newBuilder()
                                .POST(HttpRequest.BodyPublishers.ofString(engineIntegrationRequest.toJson()))
                                .uri(URI.create(url + "/" + id))
                                .header("accept", "text/plain")
                                .header("content-type", "application/json")
                                .build(),
                        HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(body -> LOG_PATH + "Sucesso ao atualizar produto na api de integração - " + body)
                .exceptionally(ex -> LOG_PATH + "Erro ao atualizar produto - " + ex.getMessage())
                .toCompletableFuture();
    }
}