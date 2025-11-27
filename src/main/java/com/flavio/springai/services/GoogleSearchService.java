package com.flavio.springai.services;

import com.fasterxml.jackson.core.JsonFactory;
import com.flavio.springai.dtos.GoogleSearchResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.customsearch.v1.Customsearch;
import com.google.api.services.customsearch.v1.model.Result;
import com.google.api.services.customsearch.v1.model.Search;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servizio per interagire con l'API di Google Search.
 */
@Service
@Slf4j
public class GoogleSearchService {

    private final RestClient restClient;
    private final String apiKey;
    private final String engineId;
    private final String baseUrl = "https://www.googleapis.com/customsearch/v1";

    public GoogleSearchService(
            @Value("${google.search.api-key}") String apiKey,
            @Value("${google.search.engine-id}") String engineId,
            RestClient.Builder restClientBuilder) {
        this.apiKey = apiKey;
        this.engineId = engineId;

        // Configura un RestClient dedicato per l'API di Google
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    /**
     * Chiama l'API di Google Search, ottiene i risultati e li converte in Spring AI Document.
     * @param query Il termine di ricerca.
     * @return Una lista di Spring AI Document.
     */
    public List<Document> searchAndRetrieve(String query) throws GeneralSecurityException, IOException {
        // Inizializza il client
        Customsearch customsearch = new Customsearch.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                null)
                .setApplicationName("My Google Search")
                .build();

        // Prepara ed esegui la richiesta
        Customsearch.Cse.List list = customsearch.cse().list();
        list.setKey(apiKey);
        list.setCx(engineId);
        list.setQ(query);
        Search results = list.execute();
        List<Result> items = results.getItems();
        if (items != null) {
            items.stream().forEach(result -> log.info("Title: " + result.getTitle() + ", Link: " + result.getLink()));
        }
        // Converti i risultati (DTO) in oggetti Spring AI Document
        return items.stream()
                .map(item -> {
                    // Combina titolo e snippet per un contesto più ricco
                    String content = item.getTitle() + "\n\n" + item.getSnippet();
                    // Aggiungi metadati utili per il RAG
                    java.util.Map<String, Object> metadata =
                            java.util.Map.of("source", "google-search", "url", item.getLink(), "title", item.getTitle());
                    return new Document(content, metadata);
                }).collect(Collectors.toList());
    }
}
