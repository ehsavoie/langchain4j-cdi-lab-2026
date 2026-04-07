package com.example.demo2;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.mistralai.MistralAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * CDI producer for the RAG ContentRetriever.
 * Ingests expedition details into an in-memory embedding store at startup.
 */
@ApplicationScoped
public class ExpeditionDetailsProvider {

    @Inject
    ExpeditionRepository repository;

    @Produces
    @Named("my-rag")
    @ApplicationScoped
    public ContentRetriever contentRetriever() {
        MistralAiEmbeddingModel embeddingModel = MistralAiEmbeddingModel.builder()
                .apiKey(System.getenv("MISTRAL_API_KEY"))
                .modelName("mistral-embed")
                .build();

        InMemoryEmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(store)
                .build();

        for (Expedition expedition : repository.listAll()) {
            ingestor.ingest(Document.from(expedition.toRagDocument(),
                    Metadata.from("expeditionId", expedition.getId())));
        }

        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(store)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .build();
    }
}
