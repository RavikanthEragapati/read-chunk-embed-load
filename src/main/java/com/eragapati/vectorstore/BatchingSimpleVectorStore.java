package com.eragapati.vectorstore;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingOptionsBuilder;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.SimpleVectorStoreContent;

import java.util.List;
import java.util.Objects;

@Slf4j
public class BatchingSimpleVectorStore extends SimpleVectorStore {


    public BatchingSimpleVectorStore(SimpleVectorStoreBuilder builder) {
        super(builder);
    }

    @Override
    public void doAdd(List<Document> documents) {
        Objects.requireNonNull(documents, "Documents list cannot be null");
        if (documents.isEmpty()) {
            throw new IllegalArgumentException("Documents list cannot be empty");
        }

        final List<String> inputs = documents.stream()
                .map(Document::getText)
                .toList();

        EmbeddingRequest embeddingRequest = new EmbeddingRequest(inputs, EmbeddingOptionsBuilder.builder().build());
        EmbeddingResponse embeddingResponse = this.embeddingModel.call(embeddingRequest);
        List<Embedding> results = embeddingResponse.getResults();

        if (results.size() != documents.size()) {
            throw new IllegalStateException("Mismatch between documents and embeddings size");
        }

        for (int i = 0; i < results.size(); i++) {
            log.info("Storing document id = {} to vector store", documents.get(i).getId());
            SimpleVectorStoreContent storeContent = new SimpleVectorStoreContent(
                    documents.get(i).getId(),
                    documents.get(i).getText(),
                    documents.get(i).getMetadata(),
                    results.get(i).getOutput());

            this.store.put(documents.get(i).getId(), storeContent);
        }
    }
}
